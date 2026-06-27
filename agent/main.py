"""
Wellness Agentic AI — Main Entry Point
=======================================
Flask-based microservice that provides AI-powered wellness trend analysis
and personalized recommendation generation.

Author: WellnessApp Team
Date: 2026-06-25

API Endpoints:
  POST /analyze — Analyze user wellness records and generate recommendations
  GET  /health  — Health check

Usage:
  python main.py
  (Runs on http://localhost:5001 by default)
"""

import os
from flask import Flask, request, jsonify
from agent import WellnessAgent

app = Flask(__name__)

# Configuration
BACKEND_URL = os.environ.get("BACKEND_URL", "http://localhost:8080")
JWT_TOKEN = os.environ.get("AGENT_JWT_TOKEN", "")

agent = WellnessAgent(backend_url=BACKEND_URL, jwt_token=JWT_TOKEN)


@app.route("/health", methods=["GET"])
def health():
    """Health check endpoint."""
    return jsonify({
        "status": "UP",
        "service": "wellness-agent",
        "version": "0.1.0"
    })


@app.route("/analyze", methods=["POST"])
def analyze():
    """
    Analyze wellness records for a user and generate recommendations.

    Request body (JSON):
    {
        "userId": 1,
        "username": "demo",
        "jwtToken": "eyJhbG...",     // JWT for backend API auth
        "backendUrl": "http://..."   // Optional: override backend URL
    }

    Response:
    {
        "success": true,
        "analysisSummary": "...",
        "sleepAnalysis": { ... },
        "activityAnalysis": { ... },
        "recommendations": [ ... ]
    }
    """
    data = request.get_json(silent=True) or {}

    user_id = data.get("userId")
    username = data.get("username")
    jwt_token = data.get("jwtToken", JWT_TOKEN)
    backend_url = data.get("backendUrl", BACKEND_URL)

    if not user_id:
        return jsonify({
            "success": False,
            "error": "userId is required"
        }), 400

    if not jwt_token:
        return jsonify({
            "success": False,
            "error": "jwtToken is required for backend authentication"
        }), 400

    try:
        result = agent.analyze_and_recommend(
            user_id=user_id,
            username=username or f"user_{user_id}",
            jwt_token=jwt_token,
            backend_url=backend_url
        )
        return jsonify(result)
    except Exception as e:
        return jsonify({
            "success": False,
            "error": str(e)
        }), 500


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5001))
    app.run(host="0.0.0.0", port=port, debug=True)
