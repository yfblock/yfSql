package io.github.yfblock.yfSql.Processor;

import com.google.auto.service.AutoService;
import io.github.yfblock.yfSql.Annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Set;


@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes(value = {
        "io.github.yfblock.yfSql.Annotation.DataRunner"
})
public class SelectProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        ProcessingEnvironment unwrapProcEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);
        Messager messager = unwrapProcEnv.getMessager();

        for(Element ele : roundEnvironment.getElementsAnnotatedWith(DataRunner.class)) {
            // 判断在哪个地方被使用
            if(!ele.getKind().equals(ElementKind.INTERFACE)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only can use @DataRunner on interface");
                return false;
            }

            TypeElement typeEle = (TypeElement) ele;

            InterfaceBuilder classBuilder = new InterfaceBuilder(typeEle);
            String packageName = ele.getEnclosingElement().toString();
            String interfaceName = ele.getSimpleName().toString();

//            for(Element subEle : ele.getEnclosedElements()) {
//                ExecutableElement functionEle = (ExecutableElement) subEle;
//                for(VariableElement vele : functionEle.getParameters()) {
//                    System.out.println(vele.getSimpleName());
//                    System.out.println(vele.asType());
//                }
//                System.out.println(functionEle.getReturnType());
//            }

            // 输出文件
            try (PrintWriter writer = new PrintWriter(
                    processingEnv
                            .getFiler()
                            .createSourceFile(packageName + "." + interfaceName + "Impl")
                            .openWriter())) {
                classBuilder.build(writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);

        // 包含以便于 idea 设置
        ProcessingEnvironment unwrapProcEnv = jbUnwrap(ProcessingEnvironment.class, procEnv);

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private static <T> T jbUnwrap(Class<? extends T> iface, T wrapper) {
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = iface.cast(unwrapMethod.invoke(null, iface, wrapper));
        }
        catch (Throwable ignored) {}
        return unwrapped != null? unwrapped : wrapper;
    }

}
