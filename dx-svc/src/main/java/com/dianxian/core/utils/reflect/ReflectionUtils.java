package com.dianxian.core.utils.reflect;

import com.google.common.collect.Lists;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 反射相关的工具类
 * @author xuwenhao
 *
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {
	/** The package separator character '.' */
	public static final char PACKAGE_SEPARATOR = '.';

	/**
	 * 是否是抽象类
	 * @author xuwenhao
	 * @param classObj
	 * @return
	 */
	public static boolean isAbstract(Class<?> classObj) {
		return Modifier.isAbstract(classObj.getModifiers());
	}
	
	/**
	 * 是否是static方法
	 * @author xuwenhao
	 * @param method
	 * @return
	 */
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
	
	/**
	 * 是否是public方法
	 * @author xuwenhao
	 * @param method
	 * @return
	 */
	public static boolean isPublic(Method method) {
		return Modifier.isPublic(method.getModifiers());
	}

	/**
	 * 是否是static
	 * @author xuwenhao
	 * @param field
	 * @return
	 */
	public static boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	/**
	 * 是否是final
	 * @author xuwenhao
	 * @param field
	 * @return
	 */
	public static boolean isFinal(Field field) {
		return Modifier.isFinal(field.getModifiers());
	}
	/**
	 * 是否是public
	 * @author xuwenhao
	 * @param field
	 * @return
	 */
	public static boolean isPublic(Field field) {
		return Modifier.isPublic((field.getModifiers()));
	}

	/**
	 * Determine the name of the package of the given class:
	 * e.g. "java.lang" for the <code>java.lang.String</code> class.
	 * @param className name of the class
	 * @return the package name, or the empty String if the class
	 * is defined in the default package
	 */
	public static String getPackageName(String className) {
		Assert.hasText(className, "ClassName must not be empty.");

		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? className.substring(0, lastDotIndex) : "");
	}

	/**
	 * 根据参数类型，查看是否有完全匹配的构造函数
	 * @param targetClass
	 * @param parameterTypes
	 * @return
	 */
	public static boolean hasConstructor(Class<?> targetClass, Class<?>...parameterTypes) {
		try {
			targetClass.getConstructor(parameterTypes);
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	/**
	 * 根据参数类型，查找完全匹配的构造函数
	 * @param targetClass
	 * @param parameterTypes
	 * @return
	 */
	public static <T> Constructor<T> getConstructorSafely(Class<T> targetClass, Class<?>...parameterTypes) {
		try {
			return targetClass.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * 根据参数类型，获取合适的Constructor，可用于查找参数类型不完全匹配，但参数类型符合类继承关系的情况。
	 * @author xuwenhao
	 * @param <T>
	 * @param targetClass
	 * @param parameters
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static <T> Constructor<T> getMatchedConstructor(Class<T> targetClass, Object... parameters) throws NoSuchMethodException {
		if (null == targetClass) {
			throw new IllegalArgumentException("Paramter targetClass is null.");
		}

		for (int i = 0; i < parameters.length; i++) {
			if (null == parameters[i]) {
				throw new IllegalArgumentException("Null in parameters, index: " + i);
			}
		}

		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}

		return getMatchedConstructor(targetClass, parameterTypes);
	}

	/**
	 * 根据参数类型，获取合适的Constructor，可用于查找参数类型不完全匹配，但参数类型符合类继承关系的情况。
	 * @author xuwenhao
	 * @param <T>
	 * @param targetClass
	 * @param parameterTypes
	 * @return
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getMatchedConstructor(Class<T> targetClass, Class<?>...parameterTypes) throws NoSuchMethodException {
		Constructor<T>[] constructors = (Constructor<T>[]) targetClass.getConstructors();
		Constructor<T> result = null;
		for(Constructor<T> constructor : constructors) {
			boolean matched = true;

			Class<?>[] consParamTypes = constructor.getParameterTypes();
			if (consParamTypes.length != parameterTypes.length) {
				// 参数长度不同，认为类型不匹配，继续下一个Constructor
				continue;
			}

			for (int i = 0; i < parameterTypes.length; i++) {
				if (!consParamTypes[i].isAssignableFrom(parameterTypes[i])) {
					// 参数类型不匹配，继续下一个Constructor
					matched = false;
					break;
				}
			}

			if (!matched) {
				continue;
			}

			result = constructor;
			break;
		}

		if (null == result) {
			throw new NoSuchMethodException(targetClass.getName() + ".<init>" + argumentTypesToString(parameterTypes));
		}

		return result;
	}

	/**
	 * 根据参数类型，获取合适的Method。
	 * @param targetClass
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getMatchedMethod(Class<?> targetClass, String methodName, Class<?>...parameterTypes)
			throws NoSuchMethodException {
		Method result = null;
		boolean hasEx = false;
		try {
			result = targetClass.getMethod(methodName, parameterTypes);
		}
		catch (Exception e) {
			hasEx = true;
		}
		
		if (!hasEx && null != result) {
			return result;
		}
		
		// 通过Class.getMethod不能找到对应的method，尝试自定义的查找方式
		Method[] methods = getAllDeclaredMethods(targetClass);
		for(Method method : methods) {
			boolean matched = isMethodMatch(method, methodName, parameterTypes);
			if (!matched) {
				continue;
			}
			
			result = method;
		}
		
		if (null == result) {
			throw new NoSuchMethodException(targetClass.getName() + argumentTypesToString(parameterTypes));
		}

		return result;
	}
	
	private static boolean isMethodMatch(Method targetMethod, String methodName, Class<?>...parameterTypes) {
		if (!targetMethod.getName().equals(methodName)) {
			// 方法名不同
			return false;
		}

		Class<?>[] methodParamTypes = targetMethod.getParameterTypes();
		if (null == parameterTypes) {									
			// 参数长度是否一致
			return (0 == methodParamTypes.length);
		}
		else {
			if (methodParamTypes.length != parameterTypes.length) {
				// 参数长度不同，认为类型不匹配
				return false;
			}

			for (int i = 0; i < parameterTypes.length; i++) {
				if (null == parameterTypes[i]) {
					// 参数为null, 无法获取类型
					continue;
				}
				if (!methodParamTypes[i].isAssignableFrom(parameterTypes[i])) {
					// 参数类型不匹配，继续下一个Method
					return false;
				}
			}
		}

		return true;
	}
	
    private static String argumentTypesToString(Class<?>[] argTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
				Class<?> c = argTypes[i];
				buf.append((c == null) ? "null" : c.getName());
            }
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * 查找最早定义方法的类.
     * @author xuwenhao
     * @param method 要查找的方法
     * @return 最早定义该方法的类
     */
	public static Class<?> getTopDeclaringClass(Method method) {
		if (null == method) {
			return null;
		}

		Class<?> result = method.getDeclaringClass();

		Class<?> superClass = result.getSuperclass();

		while (true) {
			// 当DeclaringClass已经是Object时,superClass为null
			if (null == superClass) {
				break;
			}
			try {
				// 通过getMethod来判断是否是在父类中声明的
				superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
				result = superClass;
				// 不管查找到没，为下一次循环做准备，进入上一层父类
				superClass = superClass.getSuperclass();
			} catch (SecurityException e) {
				superClass = superClass.getSuperclass();
			} catch (NoSuchMethodException e) {
				superClass = superClass.getSuperclass();
			}
		}

		return result;
	}

	/**
	 * 获取Class中定义的所有的field，包含所有父类中定义的field.
	 * @param clazz 要遍历的class
	 * @return 所有当前class以及父类中定义的filed的List
	 */
	public static List<Field> getAllDeclaredFields(Class<?> clazz) {
		List<Field> result = Lists.newArrayList();
		Class<?> classToScan = clazz;
		while (classToScan != Object.class) {
			Field[] fields = classToScan.getDeclaredFields();
			if (null != fields) {
				for (Field field : fields) {
					result.add(field);
				}
			}
			classToScan = classToScan.getSuperclass();
		}

		return result;
	}

    /**
     * 递归查找定义的field, 包含父类中定义的field
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        Field result = null;
        Class<?> classToScan = clazz;
        while (classToScan != Object.class) {
            try {
                result  = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // nothing
            }
            classToScan = classToScan.getSuperclass();
        }
        return result;
    }
}
