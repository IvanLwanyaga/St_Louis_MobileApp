package com.st_louis.data.repository

import com.st_louis.data.ApiService
import com.st_louis.data.TeacherClass
import com.st_louis.models.Student
import retrofit2.Response

class StudentsRepository(
    private val apiService: ApiService
) {

    suspend fun getTeacherStudents(teacherId: String): Response<List<Student>> {
        return apiService.getTeacherStudents(teacherId)
    }

    suspend fun getStudentsByClass(teacherId: String, className: String): Response<List<Student>> {
        return apiService.getTeacherStudentsByClass(teacherId, className)
    }

    suspend fun getTeacherClasses(teacherId: String): Response<List<TeacherClass>> {
        return apiService.getTeacherClasses(teacherId)
    }
}