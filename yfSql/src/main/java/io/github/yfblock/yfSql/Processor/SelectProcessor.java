package io.github.yfblock.yfSql.Processor;

import com.google.auto.service.AutoService;
import io.github.yfblock.yfSql.Annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;


/**
 * @author yufeng
 */
@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes(value = {
        "io.github.yfblock.yfSql.Annotation.DataRunner"
})
public class SelectProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Messager messager = processingEnv.getMessager();

        for(Element ele : roundEnvironment.getElementsAnnotatedWith(DataRunner.class)) {
            // 判断在哪个地方被使用
            if(!ele.getKind().equals(ElementKind.INTERFACE)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only can use @DataRunner on interface");
                return false;
            }

            TypeElement typeEle = (TypeElement) ele;

            String packageName = ele.getEnclosingElement().toString();
            String interfaceName = ele.getSimpleName().toString();

            // 输出文件
            try (PrintWriter writer = new PrintWriter(
                    processingEnv
                            .getFiler()
                            .createSourceFile(packageName + "." + interfaceName + "Impl")
                            .openWriter())) {
                InterfaceBuilder classBuilder = new InterfaceBuilder(typeEle, writer);
                classBuilder.build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
