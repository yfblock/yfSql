package io.github.yfblock.yfSql.Processor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.google.auto.service.AutoService;
import io.github.yfblock.yfSql.Annotation.*;
import io.github.yfblock.yfSql.Runner.MysqlRunner;
import io.github.yfblock.yfSql.Runner.SqlRunner;
import io.github.yfblock.yfSql.Sql.DataTableWrapper;
import io.github.yfblock.yfSql.Utils.ParamUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
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
            for(Element subEle : ele.getEnclosedElements()) {
                ExecutableElement functionEle = (ExecutableElement) subEle;
                for(VariableElement vele : functionEle.getParameters()) {
                    System.out.println(vele.getSimpleName());
                    System.out.println(vele.asType());
                }
                System.out.println(subEle.asType().toString());
                System.out.println(subEle.asType());
            }

            if(!ele.getKind().equals(ElementKind.INTERFACE)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only can use @DataRunner on interface");
                return false;
            }
            String packageName = ele.getEnclosingElement().toString();
            String interfaceName = ele.getSimpleName().toString();

            CompilationUnit compilationUnit = new CompilationUnit(packageName);
            compilationUnit.addImport(packageName + "." + interfaceName);

            ClassOrInterfaceDeclaration cls = compilationUnit
                    .addClass(interfaceName + "Impl")
//                    .addImplementedType(interfaceName)
                    .setPublic(true);

            try (PrintWriter writer = new PrintWriter(
                    processingEnv
                            .getFiler()
                            .createSourceFile(packageName + "." + interfaceName + "Impl")
                            .openWriter())) {
                writer.print(compilationUnit.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


//            cls.addMe

//            CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();


//            System.out.println("-------- START TYPE --------");
//            System.out.println(ele);
//            System.out.println("-------- CHILD --------");
//
//
//            for (Element subEle : ele.getEnclosedElements()) {
//                System.out.println(subEle);
//            }
//            System.out.println("-------- START TYPE --------");
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
