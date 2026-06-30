package com.st_louis.data.Repository

import com.st_louis.data.ApiService
import com.st_louis.models.ClassDetail
import com.st_louis.models.ClassSchedule
import com.st_louis.models.ClassSummary
import com.st_louis.models.Student
import com.st_louis.models.Subject
import com.st_louis.models.TeacherClass
import retrofit2.Response

class ClassRepository(
    private val apiService: ApiService = ApiService.create( )
) {

    // ==================== GET CLASSES ====================

    suspend fun getTeacherClasses(teacherId: String): Response<List<TeacherClass>> {
        return apiService.getTeacherClasses(teacherId)
    }

    suspend fun getTeacherClassesBySection(
        teacherId: String,
        section: String
    ): Response<List<TeacherClass>> {
        return apiService.getTeacherClassesBySection(teacherId, section)
    }

    suspend fun getClassDetail(classId: String): Response<ClassDetail> {
        return apiService.getClassDetail(classId)
    }

    suspend fun getClassStudents(
        teacherId: String,
        classId: String
    ): Response<List<Student>> {
        return apiService.getClassStudents(teacherId, classId)
    }

    // ==================== SCHEDULE ====================

    suspend fun getClassSchedule(
        classId: String,
        day: String? = null
    ): Response<List<ClassSchedule>> {
        return apiService.getClassSchedule(classId, day)
    }

    // ==================== SUMMARY ====================

    suspend fun getClassSummary(
        teacherId: String,
        term: String? = null,
        year: String? = null
    ): Response<ClassSummary> {
        return apiService.getClassSummary(teacherId, term, year)
    }

    // ==================== SUBJECTS ====================

    suspend fun getTeacherSubjects(teacherId: String): Response<List<Subject>> {
        return apiService.getTeacherSubjects(teacherId)
    }

    suspend fun getClassSubjects(classId: String): Response<List<Subject>> {
        return apiService.getClassSubjects(classId)
    }
}