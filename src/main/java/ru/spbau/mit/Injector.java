package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.*;


public final class Injector {

    private Map<String, Object> classStore;
    private List<String> creatingClasses;
    private final Set<String> dependences;
    private final String root;

    private Injector(String rootClassName, List<String> implementationClassNames) {
        dependences = new HashSet<>(implementationClassNames);
        creatingClasses = new ArrayList<>();
        classStore = new HashMap<>();
        root = rootClassName;
    }

    private Object createObj(String className) throws Exception {
        if (classStore.containsKey(className)) {
            return classStore.get(className);
        }

        if (creatingClasses.contains(className)) {
            throw new InjectionCycleException();
        }

        creatingClasses.add(className);

        Class<?> currentClass = Class.forName(className);
        Constructor<?> constr = currentClass.getConstructors()[0];

        ArrayList<String> depClassNames = getParamsName(constr);

        Object[] paramsObj = new Object[depClassNames.size()];
        for (int i = 0; i < depClassNames.size(); i++) {
            String name = depClassNames.get(i);
            Object param = createObj(name);
            classStore.put(name, param);
            paramsObj[i] = param;
        }

        creatingClasses.remove(className);

        return constr.newInstance(paramsObj);
    }

    private ArrayList<String> getParamsName(Constructor<?> constr) throws ClassNotFoundException,
            AmbiguousImplementationException,
            ImplementationNotFoundException,
            InjectionCycleException {
        Class<?>[] params = constr.getParameterTypes();

        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            String className = null;

            for (String implClassName : dependences) {
                Class<?> implCls = Class.forName(implClassName);
                if (params[i].isAssignableFrom(implCls)) {
                    if (className != null) {
                        throw new AmbiguousImplementationException();
                    }

                    className = implClassName;
                }
            }

            if (className == null) {
                if (params[i].isAssignableFrom(Class.forName(root))) {
                    throw new InjectionCycleException();
                }

                throw new ImplementationNotFoundException();
            }

            res.add(className);
        }

        return res;
    }


    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Injector inj = new Injector(rootClassName, implementationClassNames);
        return inj.createObj(rootClassName);
    }
}
