package org.app.users.services

import net.devh.boot.grpc.server.service.GrpcService
import org.app.grpc.*
import io.grpc.stub.StreamObserver
import org.app.users.repositories.UserRepository
import java.util.UUID

@GrpcService
class StudentGrpcService(
    private val studentRepository: UserRepository,
) : StudentServiceGrpc.StudentServiceImplBase() {

    override fun getStudentByAdmNo(
        request: AdmNoRequest,
        responseObserver: StreamObserver<StudentResponse>
    ) {
        val student = studentRepository.findByAdmNo(request.admNo)

        val response = StudentResponse.newBuilder()
            .setId(student.id.toString())
            .setName(student.name)
            .setAdmNo(student.admNo)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun studentExists(
        request: AdmNoRequest,
        responseObserver: StreamObserver<ExistsResponse>
    ) {
        val exists = studentRepository.existsByAdmNo(request.admNo)

        val response = ExistsResponse.newBuilder()
            .setExists(exists)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getAllStudents(
        request: EmptyRequest,
        responseObserver: StreamObserver<StudentResponse>
    ) {
        try {
            studentRepository.findAll().forEach { student ->
                val response = StudentResponse.newBuilder()
                    .setId(student.id.toString())
                    .setName(student.name)
                    .setAdmNo(student.admNo)
                    .build()

                responseObserver.onNext(response)
            }
            responseObserver.onCompleted()
        } catch (e: Exception) {
            responseObserver.onError(e)
        }
    }

    override fun getStudentById(
        request: StudentIdRequest,
        responseObserver: StreamObserver<StudentResponse>
    ) {
        try {
            val studentId = UUID.fromString(request.id)
            val student = studentRepository.findById(studentId).orElse(null)

            if (student != null) {
                val response = StudentResponse.newBuilder()
                    .setId(student.id.toString())
                    .setName(student.name)
                    .setAdmNo(student.admNo)
                    .build()

                responseObserver.onNext(response)
                responseObserver.onCompleted()
            } else {
                responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                        .withDescription("Student not found")
                        .asRuntimeException()
                )
            }
        } catch (e: Exception) {
            responseObserver.onError(
                io.grpc.Status.INTERNAL
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        }
    }

    override fun getStudentsByIds(
        request: StudentIdsRequest,
        responseObserver: StreamObserver<StudentsListResponse>
    ) {
        try {
            val studentIds = request.idsList.map { UUID.fromString(it) }
            val students = studentRepository.findAllById(studentIds)

            val response = StudentsListResponse.newBuilder()
                .addAllStudents(
                    students.map { student ->
                        StudentResponse.newBuilder()
                            .setId(student.id.toString())
                            .setName(student.name)
                            .setAdmNo(student.admNo)
                            .build()
                    }
                )
                .build()

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            responseObserver.onError(
                io.grpc.Status.INTERNAL
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        }
    }
}