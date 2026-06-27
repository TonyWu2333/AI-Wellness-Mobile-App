package com.wellnessapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data models for the Wellness App.
 * Maps to backend DTOs.
 *
 * @author WellnessApp Team
 */

// --- Auth ---

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("username") val username: String,
    @SerializedName("userId") val userId: Long
)

// --- Wellness Record ---

data class WellnessRecord(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("sleepHours") val sleepHours: Double? = null,
    @SerializedName("activityName") val activityName: String? = null,
    @SerializedName("activityDurationMinutes") val activityDurationMinutes: Int? = null,
    @SerializedName("recordDate") val recordDate: String,
    @SerializedName("notes") val notes: String? = null
)

// --- Chat ---

data class ChatRequest(
    @SerializedName("message") val message: String
)

data class ChatResponse(
    @SerializedName("reply") val reply: String,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class ChatMessage(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("userMessage") val userMessage: String,
    @SerializedName("botResponse") val botResponse: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

// --- Recommendation ---

data class Recommendation(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("recommendationText") val recommendationText: String,
    @SerializedName("analysisSummary") val analysisSummary: String? = null,
    @SerializedName("generatedAt") val generatedAt: String? = null,
    @SerializedName("isRead") val isRead: Boolean = false
)

// --- Common API Response Wrapper ---

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

data class ErrorResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("errorCode") val errorCode: String? = null
)
