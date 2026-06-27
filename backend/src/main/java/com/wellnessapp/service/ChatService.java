package com.wellnessapp.service;

import com.wellnessapp.dto.ChatDTOs.*;
import com.wellnessapp.entity.ChatMessage;
import com.wellnessapp.entity.User;
import com.wellnessapp.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Chatbot service — processes user messages and generates wellness-focused
 * responses. Uses a keyword-based fallback when the external AI is unavailable.
 *
 * @author WellnessApp Team
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final AIClientService aiClientService;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       AIClientService aiClientService) {
        this.chatMessageRepository = chatMessageRepository;
        this.aiClientService = aiClientService;
    }

    /**
     * Process a chat message from the user and return a response.
     */
    public ChatResponse processMessage(User user, ChatRequest request) {
        String userMessage = request.getMessage().trim();
        String reply;

        // Try AI service first, fall back to keyword-based responses
        try {
            reply = aiClientService.getChatResponse(userMessage);
        } catch (Exception e) {
            log.warn("AI service unavailable, using fallback. Error: {}", e.getMessage());
            reply = generateFallbackResponse(userMessage);
        }

        // Save conversation to database
        ChatMessage chatMessage = ChatMessage.builder()
                .user(user)
                .userMessage(userMessage)
                .botResponse(reply)
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatResponse.builder()
                .reply(reply)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    /**
     * Get chat history for the user.
     */
    public List<ChatHistoryResponse> getHistory(User user) {
        return chatMessageRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(msg -> ChatHistoryResponse.builder()
                        .id(msg.getId())
                        .userMessage(msg.getUserMessage())
                        .botResponse(msg.getBotResponse())
                        .createdAt(msg.getCreatedAt() != null ?
                                msg.getCreatedAt().toString() : null)
                        .build())
                .toList();
    }

    /**
     * Keyword-based fallback response generator when AI is unavailable.
     * Covers basic wellness topics defined in the prompt templates.
     */
    private String generateFallbackResponse(String message) {
        String lower = message.toLowerCase();

        // Sleep-related
        if (containsAny(lower, "sleep", "insomnia", "tired", "rest")) {
            return "Adults should aim for 7-9 hours of sleep per night. "
                    + "Try maintaining a consistent sleep schedule, avoiding screens "
                    + "before bedtime, and keeping your bedroom cool and dark. "
                    + "Would you like to log your sleep in the Health Records section?";
        }

        // Exercise-related
        if (containsAny(lower, "exercise", "workout", "running", "gym", "fitness",
                "activity", "cardio", "strength")) {
            return "The WHO recommends at least 150 minutes of moderate aerobic "
                    + "activity or 75 minutes of vigorous activity per week. "
                    + "Mix in strength training twice a week for best results. "
                    + "Remember to track your activities in the app!";
        }

        // Nutrition-related
        if (containsAny(lower, "diet", "food", "eat", "nutrition", "calorie",
                "protein", "vitamin", "meal", "healthy eating")) {
            return "A balanced diet should include plenty of fruits, vegetables, "
                    + "whole grains, lean proteins, and healthy fats. "
                    + "Stay hydrated by drinking at least 8 glasses of water daily. "
                    + "Consider consulting a registered dietitian for personalized advice.";
        }

        // Stress/mental health
        if (containsAny(lower, "stress", "anxiety", "mental", "meditation",
                "mindfulness", "relax", "calm")) {
            return "Managing stress is important for overall wellness. "
                    + "Try deep breathing exercises, meditation (even 5-10 minutes helps), "
                    + "regular physical activity, and maintaining social connections. "
                    + "If stress feels overwhelming, please reach out to a mental health professional.";
        }

        // Hydration
        if (containsAny(lower, "water", "hydration", "drink", "thirst", "fluid")) {
            return "Proper hydration is essential! Aim for about 2-3 liters (8-12 cups) "
                    + "of water per day, adjusting for activity level and climate. "
                    + "A good indicator: your urine should be light yellow in color.";
        }

        // General wellness / greeting
        if (containsAny(lower, "hello", "hi", "hey", "help", "wellness", "health",
                "wellbeing")) {
            return "Hello! I'm WellBot, your wellness assistant. I can help with "
                    + "topics like sleep, exercise, nutrition, stress management, "
                    + "and hydration. What would you like to know about?";
        }

        // Off-topic redirect
        return "I'm specialized in health and wellness topics. "
                + "Could you ask me something related to your wellbeing, "
                + "such as sleep, exercise, nutrition, or stress management?";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}
