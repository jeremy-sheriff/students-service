package org.app.users.services

import io.grpc.Status
import io.grpc.StatusRuntimeException
import net.devh.boot.grpc.client.inject.GrpcClient
import org.app.courses.proto.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CourseGrpcClientService {

    @GrpcClient("courseService")
    private lateinit var courseServiceStub: CourseServiceGrpc.CourseServiceBlockingStub

    private val logger = LoggerFactory.getLogger(CourseGrpcClientService::class.java)

    /**
     * Get a single course by ID
     */
    fun getCourseById(courseId: Long): CourseDto? {
        return try {
            logger.info("Fetching course with ID: $courseId")

            val request = GetCourseRequest.newBuilder()
                .setId(courseId)
                .build()

            val response = courseServiceStub.getCourse(request)

            logger.info("Successfully fetched course: ${response.course.name}")

            mapToDto(response.course)

        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                Status.Code.NOT_FOUND -> {
                    logger.warn("Course not found with ID: $courseId")
                    null
                }
                Status.Code.UNAVAILABLE -> {
                    logger.error("Course service unavailable", e)
                    throw ServiceUnavailableException("Course service is currently unavailable")
                }
                else -> {
                    logger.error("Error fetching course with ID: $courseId", e)
                    throw CourseServiceException("Failed to fetch course: ${e.status.description}")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error fetching course with ID: $courseId", e)
            throw CourseServiceException("Unexpected error: ${e.message}")
        }
    }

    /**
     * Get all courses, optionally filtered by department
     */
    fun listCourses(departmentId: Long? = null, pageSize: Int = 100, pageNumber: Int = 0): List<CourseDto> {
        return try {
            logger.info("Fetching courses list${departmentId?.let { " for department: $it" } ?: ""}")

            val requestBuilder = ListCoursesRequest.newBuilder()
                .setPageSize(pageSize)
                .setPageNumber(pageNumber)

            departmentId?.let { requestBuilder.setDepartmentId(it) }

            val request = requestBuilder.build()
            val response = courseServiceStub.listCourses(request)

            logger.info("Successfully fetched ${response.coursesCount} courses")

            response.coursesList.map { mapToDto(it) }

        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                Status.Code.UNAVAILABLE -> {
                    logger.error("Course service unavailable", e)
                    throw ServiceUnavailableException("Course service is currently unavailable")
                }
                else -> {
                    logger.error("Error fetching courses list", e)
                    throw CourseServiceException("Failed to fetch courses: ${e.status.description}")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error fetching courses list", e)
            throw CourseServiceException("Unexpected error: ${e.message}")
        }
    }

    /**
     * Create a new course
     */
    fun createCourse(name: String, departmentId: Long): CourseDto {
        return try {
            logger.info("Creating course: $name for department: $departmentId")

            val request = CreateCourseRequest.newBuilder()
                .setName(name)
                .setDepartmentId(departmentId)
                .build()

            val response = courseServiceStub.createCourse(request)

            logger.info("Successfully created course with ID: ${response.course.id}")

            mapToDto(response.course)

        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                Status.Code.NOT_FOUND -> {
                    logger.warn("Department not found with ID: $departmentId")
                    throw DepartmentNotFoundException("Department not found with ID: $departmentId")
                }
                Status.Code.ALREADY_EXISTS -> {
                    logger.warn("Course already exists: $name")
                    throw CourseAlreadyExistsException("Course already exists: $name")
                }
                Status.Code.UNAVAILABLE -> {
                    logger.error("Course service unavailable", e)
                    throw ServiceUnavailableException("Course service is currently unavailable")
                }
                else -> {
                    logger.error("Error creating course", e)
                    throw CourseServiceException("Failed to create course: ${e.status.description}")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error creating course", e)
            throw CourseServiceException("Unexpected error: ${e.message}")
        }
    }

    /**
     * Update an existing course
     */
    fun updateCourse(courseId: Long, name: String, departmentId: Long): CourseDto {
        return try {
            logger.info("Updating course ID: $courseId")

            val request = UpdateCourseRequest.newBuilder()
                .setId(courseId)
                .setName(name)
                .setDepartmentId(departmentId)
                .build()

            val response = courseServiceStub.updateCourse(request)

            logger.info("Successfully updated course with ID: ${response.course.id}")

            mapToDto(response.course)

        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                Status.Code.NOT_FOUND -> {
                    logger.warn("Course or department not found")
                    throw CourseNotFoundException("Course not found with ID: $courseId")
                }
                Status.Code.UNAVAILABLE -> {
                    logger.error("Course service unavailable", e)
                    throw ServiceUnavailableException("Course service is currently unavailable")
                }
                else -> {
                    logger.error("Error updating course", e)
                    throw CourseServiceException("Failed to update course: ${e.status.description}")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error updating course", e)
            throw CourseServiceException("Unexpected error: ${e.message}")
        }
    }

    /**
     * Delete a course
     */
    fun deleteCourse(courseId: Long): Boolean {
        return try {
            logger.info("Deleting course with ID: $courseId")

            val request = DeleteCourseRequest.newBuilder()
                .setId(courseId)
                .build()

            val response = courseServiceStub.deleteCourse(request)

            logger.info("Successfully deleted course with ID: $courseId")

            response.success

        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                Status.Code.NOT_FOUND -> {
                    logger.warn("Course not found with ID: $courseId")
                    throw CourseNotFoundException("Course not found with ID: $courseId")
                }
                Status.Code.UNAVAILABLE -> {
                    logger.error("Course service unavailable", e)
                    throw ServiceUnavailableException("Course service is currently unavailable")
                }
                else -> {
                    logger.error("Error deleting course", e)
                    throw CourseServiceException("Failed to delete course: ${e.status.description}")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error deleting course", e)
            throw CourseServiceException("Unexpected error: ${e.message}")
        }
    }

    /**
     * Get courses by department ID
     */
    fun getCoursesByDepartment(departmentId: Long): List<CourseDto> {
        return listCourses(departmentId = departmentId)
    }

    /**
     * Check if a course exists
     */
    fun courseExists(courseId: Long): Boolean {
        return getCourseById(courseId) != null
    }

    /**
     * Get multiple courses by IDs
     */
    fun getCoursesByIds(courseIds: List<Long>): List<CourseDto> {
        return courseIds.mapNotNull { courseId ->
            try {
                getCourseById(courseId)
            } catch (e: Exception) {
                logger.warn("Failed to fetch course with ID: $courseId", e)
                null
            }
        }
    }

    /**
     * Map protobuf Course to DTO
     */
    private fun mapToDto(course: Course): CourseDto {
        return CourseDto(
            id = course.id,
            name = course.name,
            departmentId = course.departmentId
        )
    }
}

// DTOs
data class CourseDto(
    val id: Long,
    val name: String,
    val departmentId: Long
)

// Custom Exceptions
class CourseServiceException(message: String) : RuntimeException(message)
class CourseNotFoundException(message: String) : RuntimeException(message)
class DepartmentNotFoundException(message: String) : RuntimeException(message)
class CourseAlreadyExistsException(message: String) : RuntimeException(message)
class ServiceUnavailableException(message: String) : RuntimeException(message)