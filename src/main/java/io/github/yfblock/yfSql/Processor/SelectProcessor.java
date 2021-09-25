package io.github.yfblock.yfSql.Processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Annotation.Insert;
import io.github.yfblock.yfSql.Annotation.Select;
import io.github.yfblock.yfSql.Runner.MysqlRunner;
import io.github.yfblock.yfSql.Runner.SqlRunner;
import io.github.yfblock.yfSql.Sql.DataTableWrapper;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Set;

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
        Messager messager = processingEnv.getMessager();

        // ? handle the DataRunner target
        for (Element ele : roundEnvironment.getElementsAnnotatedWith(DataRunner.class)) {
            DataRunnerTranslator dataRunnerTranslator =
                    new DataRunnerTranslator(mMessager, treeMaker, names, ele.getAnnotation(DataRunner.class));
            ArrayList<Class<?>> addClasses = new ArrayList<>();
            addClasses.add(SqlRunner.class);
            addClasses.add(MysqlRunner.class);
            addClasses.add(DataTableWrapper.class);
            addClasses.add(MessageFormat.class);
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
        return true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        trees = Trees.instance(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        Context context = ((JavacProcessingEnvironment) processingEnvironment).getContext();
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
                mMessager.printMessage(Diagnostic.Kind.NOTE, "hasImport");
                hasImport = true;
            } else if (compilationUnit.defs.get(i).toString().indexOf("import ") == 0 && !hasImport) {
                imports.appendList(List.from(jcTrees));
                mMessager.printMessage(Diagnostic.Kind.NOTE, "hasImport");
                hasImport = true;
            }
        }
        compilationUnit.defs = imports.toList();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
