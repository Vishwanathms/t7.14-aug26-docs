package com.example.studentservice.service;

import com.example.studentservice.exception.EmailAlreadyExistsException;
import com.example.studentservice.exception.InvalidStatusTransitionException;
import com.example.studentservice.exception.StudentNotFoundException;
import com.example.studentservice.model.Student;
import com.example.studentservice.model.StudentStatus;
import com.example.studentservice.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student rahul;

    @BeforeEach
    void setUp() {
        rahul = new Student(1L, "Rahul", "rahul@example.com");
    }

    @Test
    void getStudentByIdReturnsStudentWhenFound() {
        given(studentRepository.findById(1L)).willReturn(Optional.of(rahul));

        Student result = studentService.getStudentById(1L);

        assertThat(result.getName()).isEqualTo("Rahul");
    }

    @Test
    void getStudentByIdThrowsWhenMissing() {
        given(studentRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void createStudentDefaultsToActiveStatus() {
        given(studentRepository.existsByEmail("rahul@example.com")).willReturn(false);
        given(studentRepository.save(any(Student.class))).willAnswer(inv -> inv.getArgument(0));

        Student result = studentService.createStudent(new Student(null, "Rahul", "rahul@example.com"));

        assertThat(result.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    void createStudentThrowsWhenEmailAlreadyExists() {
        given(studentRepository.existsByEmail("rahul@example.com")).willReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(new Student(null, "Rahul", "rahul@example.com")))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(studentRepository, never()).save(any());
    }

    @Test
    void changeStatusFromActiveToInactiveSucceeds() {
        given(studentRepository.findById(1L)).willReturn(Optional.of(rahul));
        given(studentRepository.save(any(Student.class))).willAnswer(inv -> inv.getArgument(0));

        Student result = studentService.changeStatus(1L, StudentStatus.INACTIVE);

        assertThat(result.getStatus()).isEqualTo(StudentStatus.INACTIVE);
    }

    @Test
    void changeStatusFromGraduatedIsRejected() {
        Student graduated = new Student(1L, "Rahul", "rahul@example.com", StudentStatus.GRADUATED);
        given(studentRepository.findById(1L)).willReturn(Optional.of(graduated));

        assertThatThrownBy(() -> studentService.changeStatus(1L, StudentStatus.ACTIVE))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(studentRepository, never()).save(any());
    }

    @Test
    void deleteStudentThrowsWhenMissing() {
        given(studentRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void deleteStudentDeletesWhenPresent() {
        given(studentRepository.existsById(1L)).willReturn(true);

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }
}
