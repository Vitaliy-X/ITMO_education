package info.kgeorgiy.ja.gavriliuk.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;


public class StudentDB implements info.kgeorgiy.java.advanced.student.StudentQuery {
    private static final Comparator<Student> COMPARE_BY_NAME =
            Comparator.comparing(Student::getLastName)
                    .thenComparing(Student::getFirstName)
                    .thenComparing(Student::getId, Comparator.reverseOrder());

    private <T> List<T> getRequest(List<Student> students, Function<Student, T> mapper) {
        return students.stream().map(mapper).toList();
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getRequest(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getRequest(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getRequest(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return students.stream().map(student -> student.getFirstName() + " " + student.getLastName()).toList();
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream().map(Student::getFirstName).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return students.stream().sorted(Comparator.comparingInt(Student::getId)).toList();
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return students.stream().sorted(COMPARE_BY_NAME).toList();
    }

    private <T> List<Student> findRequest(Collection<Student> students, Function<Student, T> mapper, Object obj) {
        return students.stream().filter(i -> obj.equals(mapper.apply(i))).sorted(COMPARE_BY_NAME).toList();
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findRequest(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findRequest(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return findRequest(students, Student::getGroup, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return students.stream()
                .filter(student -> student.getGroup().equals(group))
                .sorted(COMPARE_BY_NAME)
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(String::compareTo)));
    }
}
