package com.example.dddsample.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

@AnalyzeClasses(packages = "com.example.dddsample", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitAllRulesTest {

    private static final String ADAPTER = "..dddsample.adapter..";
    private static final String APP = "..dddsample.app..";
    private static final String DOMAIN = "..dddsample.domain..";
    private static final String INFRA = "..dddsample.infra..";
    private static final String SHARED = "..dddsample.shared..";

    private static final Set<String> SCALAR_OR_COLLECTION_PARAMS = Set.of(
        "Long", "long", "Integer", "int", "String", "List", "Set", "Collection"
    );

    private static final Set<String> COMMAND_RETURN_ALLOWED = Set.of(
        "void", "Long", "long", "String"
    );

    private static final Set<String> QUERY_RETURN_ALLOWED = Set.of(
        "void", "boolean", "Boolean", "Long", "long", "Integer", "int",
        "String", "BigDecimal", "byte[]",
        "Optional", "List", "Set", "Collection", "Map", "PageResult"
    );

    @ArchTest
    static final ArchRule domainDoesNotDependOnOuterLayers = noClasses()
        .that().resideInAnyPackage(DOMAIN)
        .should().dependOnClassesThat().resideInAnyPackage(ADAPTER, APP, INFRA, "org.springframework..");

    @ArchTest
    static final ArchRule appDoesNotDependOnAdapterOrInfra = noClasses()
        .that().resideInAnyPackage(APP)
        .should().dependOnClassesThat().resideInAnyPackage(ADAPTER, INFRA);

    @ArchTest
    static final ArchRule adapterDoesNotDependOnInfra = noClasses()
        .that().resideInAnyPackage(ADAPTER)
        .should().dependOnClassesThat().resideInAnyPackage(INFRA);

    @ArchTest
    static final ArchRule infraDoesNotDependOnAdapter = noClasses()
        .that().resideInAnyPackage(INFRA)
        .should().dependOnClassesThat().resideInAnyPackage(ADAPTER);

    @ArchTest
    static final ArchRule sharedDoesNotDependOnBusinessLayers = noClasses()
        .that().resideInAnyPackage(SHARED)
        .should().dependOnClassesThat().resideInAnyPackage(ADAPTER, APP, DOMAIN, INFRA, "org.springframework..");

    @ArchTest
    static final ArchRule nonAdapterMustNotUseRequestContextOrBaseResponse = noClasses()
        .that().resideInAnyPackage(APP, DOMAIN, INFRA)
        .should().dependOnClassesThat().haveSimpleName("RequestContext")
        .orShould().dependOnClassesThat().haveSimpleName("BaseResponse");

    @ArchTest
    static final ArchRule appDtosMustBeRecords = classes()
        .that().resideInAnyPackage("..dddsample.app..command.dto..", "..dddsample.app..query.dto..")
        .should().beRecords();

    @ArchTest
    static final ArchRule valueObjectsMustBeRecordsAndAtMost4Fields = classes()
        .that().resideInAnyPackage("..dddsample.domain..valueobject..")
        .should(new ArchCondition<>("be record with at most 4 fields and end with VO") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                if (!javaClass.isRecord() || !javaClass.getSimpleName().endsWith("VO")) {
                    events.add(SimpleConditionEvent.violated(javaClass,
                        javaClass.getFullName() + " must be a record ending with VO"));
                }
                long fieldCount = javaClass.getFields().stream()
                    .filter(f -> !f.getModifiers().contains(JavaModifier.STATIC))
                    .count();
                if (fieldCount > 4) {
                    events.add(SimpleConditionEvent.violated(javaClass,
                        javaClass.getFullName() + " has " + fieldCount + " fields (max 4)"));
                }
            }
        });

    @ArchTest
    static final ArchRule commandServiceSignatureWhitelist = classes()
        .that().resideInAnyPackage("..dddsample.app..command..")
        .and().areInterfaces()
        .and().haveSimpleNameEndingWith("CommandService")
        .should(new ArchCondition<>("follow Rule 1 & Rule 2 signature constraints") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaMethod m : javaClass.getMethods()) {
                    if (!m.getModifiers().contains(JavaModifier.PUBLIC)
                            || m.getModifiers().contains(JavaModifier.SYNTHETIC)) {
                        continue;
                    }
                    String returnName = m.getRawReturnType().getSimpleName();
                    if (!COMMAND_RETURN_ALLOWED.contains(returnName) && !returnName.endsWith("CommandResult")) {
                        events.add(SimpleConditionEvent.violated(javaClass,
                            javaClass.getSimpleName() + "." + m.getName() + " returns disallowed type: " + returnName));
                    }
                    int scalarCount = 0;
                    int commandCount = 0;
                    for (JavaClass p : m.getRawParameterTypes()) {
                        String pName = p.getSimpleName();
                        if (pName.endsWith("Command")) {
                            commandCount++;
                        } else if (SCALAR_OR_COLLECTION_PARAMS.contains(pName)) {
                            scalarCount++;
                        } else {
                            events.add(SimpleConditionEvent.violated(javaClass,
                                javaClass.getSimpleName() + "." + m.getName() + " uses disallowed param: " + pName));
                        }
                    }
                    if (scalarCount > 2 || commandCount > 1) {
                        events.add(SimpleConditionEvent.violated(javaClass,
                            javaClass.getSimpleName() + "." + m.getName()
                                + " exceeds parameter limit (max 2 context scalars + max 1 *Command)"));
                    }
                }
            }
        });

    @ArchTest
    static final ArchRule queryServiceSignatureWhitelist = classes()
        .that().resideInAnyPackage("..dddsample.app..query..")
        .and().areInterfaces()
        .and().haveSimpleNameEndingWith("QueryService")
        .should(new ArchCondition<>("follow Rule 1 & Rule 2 query signature constraints") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaMethod m : javaClass.getMethods()) {
                    if (!m.getModifiers().contains(JavaModifier.PUBLIC)
                            || m.getModifiers().contains(JavaModifier.SYNTHETIC)) {
                        continue;
                    }
                    String returnName = m.getRawReturnType().getSimpleName();
                    if (!QUERY_RETURN_ALLOWED.contains(returnName)
                            && !returnName.endsWith("QueryResult")
                            && !returnName.endsWith("Item")) {
                        events.add(SimpleConditionEvent.violated(javaClass,
                            javaClass.getSimpleName() + "." + m.getName() + " returns disallowed type: " + returnName));
                    }
                    int scalarCount = 0;
                    int queryCount = 0;
                    for (JavaClass p : m.getRawParameterTypes()) {
                        String pName = p.getSimpleName();
                        if (pName.endsWith("Query")) {
                            queryCount++;
                        } else if (SCALAR_OR_COLLECTION_PARAMS.contains(pName)) {
                            scalarCount++;
                        } else {
                            events.add(SimpleConditionEvent.violated(javaClass,
                                javaClass.getSimpleName() + "." + m.getName() + " uses disallowed param: " + pName));
                        }
                    }
                    if (scalarCount > 2 || queryCount > 1) {
                        events.add(SimpleConditionEvent.violated(javaClass,
                            javaClass.getSimpleName() + "." + m.getName()
                                + " exceeds parameter limit (max 2 context scalars + max 1 *Query)"));
                    }
                }
            }
        });

    @ArchTest
    static final ArchRule commandServiceImplPublicMethodsMustBeTransactional = classes()
        .that().resideInAnyPackage("..dddsample.app..command.impl..")
        .and().haveSimpleNameEndingWith("CommandServiceImpl")
        .should(new ArchCondition<>("have all public methods annotated with @Transactional") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                javaClass.getMethods().stream()
                    .filter(m -> m.getModifiers().contains(JavaModifier.PUBLIC))
                    .filter(m -> !m.getModifiers().contains(JavaModifier.STATIC))
                    .filter(m -> !m.getModifiers().contains(JavaModifier.SYNTHETIC))
                    .filter(m -> !m.isAnnotatedWith(Transactional.class))
                    .forEach(m -> events.add(SimpleConditionEvent.violated(javaClass,
                        javaClass.getSimpleName() + "." + m.getName() + " must be annotated with @Transactional")));
            }
        });
}
