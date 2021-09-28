package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.DataRunner;
import io.github.yfblock.yfSql.Runner.MysqlRunner;
import io.github.yfblock.yfSql.Runner.SqliteRunner;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;

public class DataRunnerTranslator extends TreeTranslator {
    private final Messager    messager;
    private final TreeMaker   treeMaker;
    private final Names       names;
    private final DataRunner  dataRunner;

    public DataRunnerTranslator(Messager messager, TreeMaker treeMaker, Names names, DataRunner dataRunner) {
        this.messager   = messager;
        this.treeMaker  = treeMaker;
        this.names      = names;
        this.dataRunner = dataRunner;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl tree) {
        super.visitClassDef(tree);

        JCTree.JCVariableDecl jcVariableDecl = addSqlRunner();
        if(jcVariableDecl.init == null) tree.defs = tree.defs.append(addConstructor());
        else tree.defs = tree.defs.append(addBlankConstructor());
        tree.defs = tree.defs.prepend(jcVariableDecl);
    }

    private JCTree.JCMethodDecl addBlankConstructor() {
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.GENERATEDCONSTR | Flags.PUBLIC),
                names.init,
                null,
                List.nil(),
                List.nil(),
                List.nil(),
                treeMaker.Block(0, List.nil()),
                null
        );
    }


    private JCTree.JCMethodDecl addConstructor() {
        // params List
        ArrayList<JCTree.JCVariableDecl> params = new ArrayList<>();
        params.add(treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PARAMETER),
                names.fromString("sqlRunner"),
                treeMaker.Ident(names.fromString("SqlRunner")),
                null));

        // code block
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        Name classParam = names.fromString("sqlRunner");
        statements.add(treeMaker.Exec(treeMaker.Assign(treeMaker.Select(treeMaker.Ident(names.fromString("this")),classParam),
                treeMaker.Ident(classParam))));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.GENERATEDCONSTR | Flags.PUBLIC),
                names.init,
                null,
                List.nil(),
                List.from(params),
                List.nil(),
                body,
                null
        );
    }

    /**
     * add sqlRunner param
     * @return
     */
    private JCTree.JCVariableDecl addSqlRunner() {
        if(dataRunner.database().equals("")&&dataRunner.path().equals("")) {
            // if don't set anything, then only add the field
            return treeMaker.VarDef(
                    treeMaker.Modifiers(Flags.PRIVATE),
                    names.fromString("sqlRunner"),
                    treeMaker.Ident(names.fromString("SqlRunner")),
                    null);
        }

        ArrayList<JCTree.JCExpression> args = new ArrayList<>();

        String runnerSimpleName = null;
        String runnerFullName = "";
        try {
            Class clz = dataRunner.runner();
            runnerSimpleName = clz.getSimpleName();
            runnerFullName = clz.getCanonicalName();
        } catch (MirroredTypeException e){
            DeclaredType classTypeMirror = (DeclaredType) e.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            runnerSimpleName = classTypeElement.getSimpleName().toString();
            runnerFullName = classTypeElement.getQualifiedName().toString();
        }

        if(runnerFullName.equals(MysqlRunner.class.getCanonicalName())) {
            if(!dataRunner.useProperty()) {
                args.add(treeMaker.Literal(dataRunner.hostname()));
                args.add(treeMaker.Literal(dataRunner.port()));
                args.add(treeMaker.Literal(dataRunner.username()));
                args.add(treeMaker.Literal(dataRunner.password()));
                args.add(treeMaker.Literal(dataRunner.database()));
            }
        } else if (runnerFullName.equals(SqliteRunner.class.getCanonicalName())) {
            args.add(treeMaker.Literal(dataRunner.path()));
        }

        JCTree.JCExpression init = treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Ident(names.fromString(runnerSimpleName)),
                List.from(args),
                null);

        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString("sqlRunner"),
                treeMaker.Ident(names.fromString("SqlRunner")),
                init);
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree)  {
        super.visitMethodDef(tree);
    }

    /**
     * * 访问代码块
     * */
    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);
    }
}
