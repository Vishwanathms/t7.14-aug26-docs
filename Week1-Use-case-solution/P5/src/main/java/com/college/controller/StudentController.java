package com.college.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.college.model.Student;
import com.college.service.StudentService;

@Controller
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/students";
    }

    @GetMapping("/students")
    public String listStudents(Model model) {

        model.addAttribute("students", studentService.viewStudents());
        return "list";

    }

    @GetMapping("/students/add")
    public String showAddForm(Model model) {

        model.addAttribute("student", new Student());
        model.addAttribute("editMode", false);
        return "form";

    }

    @PostMapping("/students/add")
    public String addStudent(@ModelAttribute("student") Student student) {

        studentService.addStudent(student);
        return "redirect:/students";

    }

    @GetMapping("/students/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {

        Student student = studentService.searchStudent(id);

        if (student == null) {
            return "redirect:/students";
        }

        model.addAttribute("student", student);
        model.addAttribute("editMode", true);
        return "form";

    }

    @PostMapping("/students/edit/{id}")
    public String updateStudent(@PathVariable int id, @ModelAttribute("student") Student student) {

        student.setId(id);
        studentService.updateStudent(student);
        return "redirect:/students";

    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable int id) {

        studentService.deleteStudent(id);
        return "redirect:/students";

    }

    @GetMapping("/students/stats")
    public String stats(Model model) {

        model.addAttribute("topper", studentService.displayHighestMarks());
        model.addAttribute("average", studentService.calculateAverage());
        return "stats";

    }

}
