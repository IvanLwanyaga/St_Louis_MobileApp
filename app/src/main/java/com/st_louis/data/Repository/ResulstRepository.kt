package com.st_louis.data.repository

import com.st_louis.data.ApiService
import com.st_louis.models.*
import retrofit2.Response

class ResultsRepository(
    private val apiService: ApiService
) {

    suspend fun getExams(teacherId: String): Response<List<Exam>> {
        return apiService.getExams(teacherId)
    }

    suspend fun getTeacherClasses(teacherId: String, section: String): Response<List<TeacherClass>> {
        return apiService.getTeacherClassesBySection(teacherId, section)
    }

    suspend fun getStudentsByClass(teacherId: String, classId: String): Response<List<Student>> {
        return apiService.getClassStudents(teacherId, classId)
    }

    suspend fun submitResults(resultsData: Map<String, Any>): Response<ApiResponse<Any>> {
        // Convert map to ResultsRequest
        val request = ResultsRequest(
            teacher_id = resultsData["teacher_id"] as String,
            class_id = resultsData["class_id"] as String,
            exam_id = resultsData["exam_id"] as String,
            subject = resultsData["subject"] as String,
            section = resultsData["section"] as String,
            term = resultsData["term"] as String,
            year = resultsData["year"] as String,
            results = (resultsData["results"] as List<Map<String, Any>>).map {
                ResultRecord(
                    student_id = it["student_id"] as String,
                    score = it["score"] as Int,
                    grade = it["grade"] as String,
                    remarks = it["remarks"] as? String
                )
            }
        )
        return apiService.submitResults(request)
    }

    // Additional methods for Results
    suspend fun getClassResults(
        teacherId: String,
        classId: String,
        examId: String
    ): Response<List<StudentResult>> {
        return apiService.getClassResults(teacherId, classId, examId)
    }

    suspend fun updateResult(
        resultId: String,
        resultData: ResultUpdateRequest
    ): Response<ApiResponse<Any>> {
        return apiService.updateResult(resultId, resultData)
    }

    suspend fun deleteResult(resultId: String): Response<SimpleApiResponse> {
        return apiService.deleteResult(resultId)
    }
}