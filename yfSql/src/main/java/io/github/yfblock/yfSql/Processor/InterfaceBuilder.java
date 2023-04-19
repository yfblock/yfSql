package io.github.yfblock.yfSql.Processor;

import io.github.yfblock.yfSql.Annotation.*;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yufeng
 */
public class InterfaceBuilder {

    // The element will be handled
    private final TypeElement element;
    private final String interfaceName;
    private final PrintWriter writer;

    /**
     * Constructor
     * @param element the class Element with @DataRunner
     * @param writer source file writer
     */
    public InterfaceBuilder(TypeElement element, PrintWriter writer) {
        this.element  = element;
        this.writer   = writer;
        this.interfaceName = element.getSimpleName().toString();
    }

    /**
     *
     * @param sql the sql will be built
     * @param params interface method params
     * @return the statement will be used
     */
    protected String buildSqlAndParams(String sql, String params) {
        System.out.println(params);
        // replace sql to correct String
        // transfer " to \" in the sql, ' to '' for MessageFormat
        sql = sql.replaceAll("\"", "\\\\\"").replaceAll("'", "''");
        params = params.trim();
        if("".equals(params)) {
            return "\"" + sql + "\"";
        } else {
            return "MessageFormat.format(\"" + sql + "\", " + params + ")";
        }
    }

    /**
     * generate sql and related code according to interface method and its annotation
     * @param executableElement interface method Element with @Select, @Insert or ....
     */
    protected void buildSql(ExecutableElement executableElement) {
        // 判断是否需要 return
        if (!"void".equals(executableElement.getReturnType().toString())) {
            writer.print("return ");
        }

        ArrayList<Name> parameters = new ArrayList<>();
        for(VariableElement variableElement : executableElement.getParameters()) {
            parameters.add(variableElement.getSimpleName());
        }
        String parameterString = String.join(",", parameters);

        Select select = executableElement.getAnnotation(Select.class);
        if(select != null) {
            String returnType = executableElement.getReturnType().toString();
            writer.println(MessageFormat.format(
                    "this.executeQuery({0}, {1}.class);",
                    this.buildSqlAndParams(select.value(), parameterString),
                    returnType.substring(returnType.indexOf('<') + 1, returnType.lastIndexOf('>'))
            ));
            return;
        }
        Insert insert = executableElement.getAnnotation(Insert.class);
        if(insert != null) {
            writer.println(MessageFormat.format(
                    "this.execute({0});",
                    this.buildSqlAndParams(insert.value(), parameterString)
            ));
            return;
        }
        Delete delete = executableElement.getAnnotation(Delete.class);
        if(delete != null) {
            writer.println(MessageFormat.format(
                    "this.execute({0});",
                    this.buildSqlAndParams(delete.value(), parameterString)
            ));
            return;
        }
        Update update = executableElement.getAnnotation(Update.class);
        if(update != null) {
            writer.println(MessageFormat.format(
                    "this.execute({0});",
                    this.buildSqlAndParams(update.value(), parameterString)
            ));
            return;
        }
        // TODO: add a feature, just select one row
        Find find = executableElement.getAnnotation(Find.class);
        if(find != null) {
            String returnType = executableElement.getReturnType().toString();
            writer.println(MessageFormat.format(
                    "this.executeQueryFind({0}, {1}.class);",
                    this.buildSqlAndParams(find.value(), parameterString),
                    returnType
            ));
        }
    }

    /**
     * write interface methods to writer
     */
    protected void writeInterfaceMethods() {
        this.element.getEnclosedElements().stream()
            .map(ele -> (ExecutableElement) ele)
            .forEach(ele -> {
                // parameters build
                ArrayList<String> parameters = new ArrayList<>();
                for(VariableElement variableElement : ele.getParameters()) {
                    parameters.add(variableElement.asType() + " " + variableElement.getSimpleName());
                }
                // parameters build end

                // get return type
                String returnType = ele.getReturnType().toString();

                // function header
                writer.write(MessageFormat.format(
                        "public {0} {1}({2})",
                        returnType,
                        ele.getSimpleName(),
                        String.join(",", parameters)
                ));

                // write exceptions
                if (ele.getThrownTypes().size() > 0) {
                    List<String> exceptions = ele.getThrownTypes().stream()
                            .map(TypeMirror::toString).collect(Collectors.toList());
                    writer.write(" throws " + String.join(",", exceptions));
                }

                // write start of content
                writer.println(" {");

                // function content build statement for sql
                this.buildSql(ele);

                // function end
                writer.println("}");
            });
    }

    /**
     * write imports String to writer
     */
    protected void writeImports() {
        writer.println("import io.github.yfblock.yfSql.Sql.DataTableWrapper;");
        writer.println("import java.text.MessageFormat;");
        writer.println("import javax.sql.DataSource;");
    }

    /**
     * write the target class and its dependencies string to writer
     */
    public void build() {
        // write package.
        writer.println("package " + this.element.getEnclosingElement().toString() + ";");

        this.writeImports();
        for(AnnotationMirror annotationMirror: this.element.getAnnotationMirrors()) {
            if("@io.github.yfblock.yfSql.Annotation.DataRunner".equals(annotationMirror.toString())) {
                continue;
            }
            writer.println(annotationMirror);
        }
        writer.println(MessageFormat.format("public class {0}Impl extends DataTableWrapper implements {0} '{'", interfaceName));
        
        // write constructors
        writer.println("public "+interfaceName+"Impl() {}");
        writer.println("public "+interfaceName+"Impl(DataSource dataSource) {this.dataSource = dataSource;}");

        this.writeInterfaceMethods();
        writer.println("}");
    }
}
