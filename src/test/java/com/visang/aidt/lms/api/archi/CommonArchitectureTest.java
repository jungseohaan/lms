package com.visang.aidt.lms.api.archi;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.Profile;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@Profile("dev")
class CommonArchitectureTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "com.visang.aidt.lms.api.act",
            "com.visang.aidt.lms.api.article",
            "com.visang.aidt.lms.api.assessment",
            "com.visang.aidt.lms.api.bookmark",
            "com.visang.aidt.lms.api.common",
            //"com.visang.aidt.lms.api.configuration", // 제외
            //"com.visang.aidt.lms.api.contents", // 제외
            "com.visang.aidt.lms.api.dashboard",
            "com.visang.aidt.lms.api.engtemp",
            //"com.visang.aidt.lms.api.engvocal", // 제외
            "com.visang.aidt.lms.api.homework",
            "com.visang.aidt.lms.api.keris",
            "com.visang.aidt.lms.api.learning",
            "com.visang.aidt.lms.api.lecture",
            "com.visang.aidt.lms.api.library",
            //"com.visang.aidt.lms.api.log", // 제외
            "com.visang.aidt.lms.api.materials",
            "com.visang.aidt.lms.api.mathexplore",
            "com.visang.aidt.lms.api.mathvillage",
            "com.visang.aidt.lms.api.media",
            //"com.visang.aidt.lms.api.member.dto", // 제외
            "com.visang.aidt.lms.api.mq",
            "com.visang.aidt.lms.api.notification",
            //"com.visang.aidt.lms.api.repository", // 제외
            //"com.visang.aidt.lms.api.sample.vo", // 제외
            "com.visang.aidt.lms.api.scenario",
            "com.visang.aidt.lms.api.selflrn",
            "com.visang.aidt.lms.api.shop",
            "com.visang.aidt.lms.api.socket",
            //"com.visang.aidt.lms.api.stress", // 제외
            "com.visang.aidt.lms.api.system",
            "com.visang.aidt.lms.api.textbook",
            "com.visang.aidt.lms.api.user",
            //"com.visang.aidt.lms.api.utility", // 제외
            //"com.visang.aidt.lms.api.web", // 제외
            "com.visang.aidt.lms.api.wrongnote"
    })
    void controllers_should_not_access_mappers_directly(String targetPackage) {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(targetPackage);

        // Controller에서 직접 Mapper 호출하지 않도록 해야 함
        noClasses().that().resideInAPackage(".." + targetPackage + ".controller..")
                .should().accessClassesThat().resideInAPackage(".." + targetPackage + ".mapper..")
                .check(classes);
    }

    /*@Test
    void configuration_classes_should_end_with_Config() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.visang.aidt.lms.api.configuration");

        // configuration package에는 파일명이 Config, Type, DataSource로 끝나야 함
        classes().that().resideInAPackage("com.visang.aidt.lms.api.configuration")
                .should().haveSimpleNameEndingWith("Config")
                .orShould().haveSimpleNameEndingWith("Type")
                .orShould().haveSimpleNameEndingWith("DataSource")
                .check(classes);
    }*/

    // @Loggable 관련해서는 기본적인 규칙이 Controller의 메소드에 붙이는 게 맞으나
    // 빠져 있는 메소드가 많아서 추후 판단하여 추가 여부에 따라 검사 로직 추가
//    @Test
//    void controller_methods_should_have_Loggable_annotation() {
//        JavaClasses classes = new ClassFileImporter()
//                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
//                .importPackages("com.visang.aidt.lms.api");
//
//        // Controller 클래스에서 @Loggable 어노테이션 추가되어야 함
//        ArchRuleDefinition.methods().that()
//                .areDeclaredInClassesThat().haveSimpleNameEndingWith("Controller")
//                .should().beAnnotatedWith(Loggable.class)
//                .check(classes);
//    }

    @Test
    void service_methods_should_not_have_Loggable_annotation() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.visang.aidt.lms.api");

        // Service 클래스에서 @Loggable 어노테이션이 없어야 함
        ArchRuleDefinition.methods().that()
                .areDeclaredInClassesThat().haveSimpleNameEndingWith("Service")
                .should().notBeAnnotatedWith(Loggable.class)
                .check(classes);
    }


}