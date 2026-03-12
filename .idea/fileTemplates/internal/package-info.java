#parse("File Header.java")
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")@NullMarked
package ${PACKAGE_NAME};

import org.jspecify.annotations.NullMarked;#end