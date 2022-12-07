package io.github.yfblock.yfSql.Processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.*;
import io.github.yfblock.yfSql.Runner.MysqlRunner;
import io.github.yfblock.yfSql.Runner.SqlRunner;
import io.github.yfblock.yfSql.Sql.DataTableWrapper;
import io.github.yfblock.yfSql.Utils.ParamUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Set;


@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes(value = {
        "io.github.yfblock.yfSql.Annotation.Select",
        "io.github.yfblock.yfSql.Annotation.DataRunner"
})
public class SelectProcessor extends AbstractProcessor {

    private Trees trees;
    private Messager mMessager;  //用于打印数据
    private TreeMaker treeMaker;//TreeMaker 封装了创建AST节点的一些方法
    private Names names;        //提供了创建标识符的方法

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        ProcessingEnvironment unwrapProcEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);

        Messager messager = unwrapProcEnv.getMessager();

        // ? handle the DataRunner target
        for (Element ele : roundEnvironment.getElementsAnnotatedWith(DataRunner.class)) {
            DataRunnerTranslator dataRunnerTranslator =
                    new DataRunnerTranslator(mMessager, treeMaker, names, ele.getAnnotation(DataRunner.class));
            ArrayList<Class<?>> addClasses = new ArrayList<>();
            addClasses.add(SqlRunner.class);
            addClasses.add(MysqlRunner.class);
            addClasses.add(DataTableWrapper.class);
            addClasses.add(MessageFormat.class);
            addClasses.add(ParamUtil.class);
            addImports(ele, addClasses);
            JCTree tree = (JCTree) trees.getTree(ele);
            tree.accept(dataRunnerTranslator);
        }

        for (Element ele : roundEnvironment.getElementsAnnotatedWith(Select.class)) {
            if (ele.getKind() == ElementKind.METHOD) {
                JCTree tree = (JCTree) trees.getTree(ele);
                SelectTranslator selectTranslator = new SelectTranslator(mMessager, treeMaker, names, ele.getAnnotation(Select.class));
                tree.accept(selectTranslator);
            }
        }

        for (Element ele : roundEnvironment.getElementsAnnotatedWith(Insert.class)) {
            if (ele.getKind() == ElementKind.METHOD) {
                JCTree tree = (JCTree) trees.getTree(ele);
                InsertTranslator insertTranslator = new InsertTranslator(mMessager, treeMaker, names, ele.getAnnotation(Insert.class));
                tree.accept(insertTranslator);
            }
        }

        for (Element ele : roundEnvironment.getElementsAnnotatedWith(Update.class)) {
            if (ele.getKind() == ElementKind.METHOD) {
                JCTree tree = (JCTree) trees.getTree(ele);
                UpdateTranslator updateTranslator = new UpdateTranslator(mMessager, treeMaker, names, ele.getAnnotation(Update.class));
                tree.accept(updateTranslator);
            }
        }

        for (Element ele : roundEnvironment.getElementsAnnotatedWith(Delete.class)) {
            if (ele.getKind() == ElementKind.METHOD) {
                JCTree tree = (JCTree) trees.getTree(ele);
                DeleteTranslator deleteTranslator = new DeleteTranslator(mMessager, treeMaker, names, ele.getAnnotation(Delete.class));
                tree.accept(deleteTranslator);
            }
        }
        return true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);

        // 包含以便于 idea 设置
        ProcessingEnvironment unwrapProcEnv = jbUnwrap(ProcessingEnvironment.class, procEnv);
//        procEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format(
//                "You aren't using a compiler supported by lombok, so lombok will not work and has been disabled.\n" +
//                        "Your processor is: %s\n", unwrapProcEnv.getClass().getName()));

        trees = Trees.instance(unwrapProcEnv);
        mMessager = unwrapProcEnv.getMessager();
        Context context = ((JavacProcessingEnvironment) unwrapProcEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    private void addSQLQuery() {
    }

    /**
     * add imports
     * @param element       class element
     * @param classes       the classes will be imported
     */
    private void addImports(Element element, ArrayList<Class<?>> classes) {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(element).getCompilationUnit();
       ArrayList<JCTree> jcTrees = new ArrayList<>();
        for(Class<?> cls : classes) {
            JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(cls.getPackage().getName())), names.fromString(cls.getSimpleName()));
            JCTree.JCImport jcImport = treeMaker.Import(fieldAccess, false);
            jcTrees.add(jcImport);
        }
        ListBuffer<JCTree> imports = new ListBuffer<>();
        Boolean hasImport = false;
        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
            if (compilationUnit.defs.get(i).toString().indexOf("package ") == 0 && !hasImport) {
                imports.appendList(List.from(jcTrees));
                hasImport = true;
            } else if (compilationUnit.defs.get(i).toString().indexOf("import ") == 0 && !hasImport) {
                imports.appendList(List.from(jcTrees));
                hasImport = true;
            }
        }
        compilationUnit.defs = imports.toList();
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
