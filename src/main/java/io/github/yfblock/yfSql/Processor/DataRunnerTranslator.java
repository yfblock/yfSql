package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.code.Flags;
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
import java.util.ArrayList;

/**
 * DataRunner注解处理器
 */
public class DataRunnerTranslator extends TreeTranslator {
    private final Messager    messager;
    private final TreeMaker   treeMaker;
    private final Names       names;
    private final DataRunner  dataRunner;

    private boolean hasSqlRunner = false;   // 判断是否存在sqlRunner字段

    public DataRunnerTranslator(Messager messager, TreeMaker treeMaker, Names names, DataRunner dataRunner) {
        this.messager   = messager;
        this.treeMaker  = treeMaker;
        this.names      = names;
        this.dataRunner = dataRunner;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl tree) {
        super.visitClassDef(tree);

        // 如果已经存在sqlRunner 则 不再添加
        if(hasSqlRunner) return;

        // 如果不存在sqlRunner 则构建后添加
        JCTree.JCVariableDecl jcVariableDecl = addSqlRunner();
        System.out.println(jcVariableDecl);
        if(jcVariableDecl.init == null) tree.defs = tree.defs.append(addConstructor());
//        else tree.defs = tree.defs.append(addBlankConstructor());
        tree.defs = tree.defs.prepend(jcVariableDecl);
    }

    /**
     * 添加空的构造函数
     * @return 返回构造函数方法描述体
     */
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


    /**
     * 添加构造函数
     * @return 构造函数方法描述体
     */
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
     * @return 变量方法描述体
     */
    private JCTree.JCVariableDecl addSqlRunner() {
        // 判断类的DataRunner注解是否为空
        if(dataRunner.database().equals("")&&dataRunner.path().equals("")) {
            // if don't set anything, then only add the field
            return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString("sqlRunner"),
                treeMaker.Ident(names.fromString("SqlRunner")),
                null
            );
        }

        ArrayList<JCTree.JCExpression> args = new ArrayList<>();

        String runnerSimpleName;
        String runnerFullName;
        try {
            Class<?> clz = dataRunner.runner();
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
            null
        );

        return treeMaker.VarDef(
            treeMaker.Modifiers(Flags.PRIVATE),
            names.fromString("sqlRunner"),
            treeMaker.Ident(names.fromString("SqlRunner")),
            init
        );
    }

    /**
     * 遍历方法定义
     */
    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree)  {
        super.visitMethodDef(tree);
        System.out.println(tree.getName());
    }

    /**
     * 遍历变量列表
     */
    @Override
    public void visitVarDef(JCTree.JCVariableDecl tree) {
        super.visitVarDef(tree);

        // 判断sqlRunner字段是否已经存在
        if(tree.getName().toString().equals("sqlRunner")) {
            this.hasSqlRunner = true;
        }
    }

    /**
     * * 遍历代码块
     * */
    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);
    }
}
