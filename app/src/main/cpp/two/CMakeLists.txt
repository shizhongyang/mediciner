#继承上一层的CMakeLists.txt的变量，也可以在这里重新赋值
#C 的编译选项是 CMAKE_C_FLAGS
# 指定编译参数，可选
#SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")

set(libjpeg_dir ${CMAKE_SOURCE_DIR}/two/libjpeg)
set(INC_DIR ${libjpeg_dir}/include)
set(libjpeg_lib_dir ${libjpeg_dir}/prebuilt)

#添加头文件目录
include_directories(${INC_DIR})

set(SOURCE_FILES compress.c)

add_library(compress SHARED
            ${SOURCE_FILES})

find_library(log-lib log)
find_library(graphics jnigraphics)

add_library(libjpeg SHARED IMPORTED)
set_target_properties(libjpeg PROPERTIES IMPORTED_LOCATION ${libjpeg_lib_dir}/${ANDROID_ABI}/libjpeg.so)

target_link_libraries(compress libjpeg ${log-lib} ${graphics})