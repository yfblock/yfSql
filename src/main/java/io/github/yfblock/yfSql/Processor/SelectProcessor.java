package io.github.yfblock.yfSql.Processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
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
import java.text.MessageFormat;
import java.util.Set;

@SupportedAnnotationTypes(value = {
        "io.github.yfblock.yfSql.Annotation.Select",
        "io.github.yfblock.yfSql.Annotation.DataRunner"
})
@SupportedSourceVersion(value = SourceVersion.RELEASE_11)
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
            addImports(ele, SqlRunner.class);
            addImports(ele, MysqlRunner.class);
            addImports(ele, DataTableWrapper.class);
            addImports(ele, MessageFormat.class);
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
     * @param importClass   the class will be imported
     */
    private void addImports(Element element, Class<?> importClass) {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(element).getCompilationUnit();
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(importClass.getPackage().getName())), names.fromString(importClass.getSimpleName()));
        JCTree.JCImport jcImport = treeMaker.Import(fieldAccess, false);
        ListBuffer<JCTree> imports = new ListBuffer<>();
        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
            if (compilationUnit.defs.get(i).toString().indexOf("package ") == 0) imports.append(jcImport);
        }
        compilationUnit.defs = imports.toList();
    }
}
