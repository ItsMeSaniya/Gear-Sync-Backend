#!/usr/bin/env python3
"""
Test script for GearSync Chatbot Service
Run this script to test the chatbot API endpoints
"""

import requests
import json
import time
import sys

# Configuration
BASE_URL = "http://localhost:8005"
TEST_MESSAGES = [
    "Hello, I need help with my car",
    "What services do you offer?",
    "I want to schedule an oil change",
    "How much does a brake inspection cost?",
    "Can you help me with my appointment?"
]

def test_health_check():
    """Test the health check endpoint"""
    print("🔍 Testing health check...")
    try:
        response = requests.get(f"{BASE_URL}/health", timeout=10)
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Health check passed: {data['status']}")
            return True
        else:
            print(f"❌ Health check failed: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"❌ Health check error: {e}")
        return False

def test_simple_chat():
    """Test the simple chat endpoint"""
    print("\n💬 Testing simple chat...")
    try:
        response = requests.post(
            f"{BASE_URL}/chat/simple",
            json={"message": "Hello, test message"},
            timeout=30
        )
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Simple chat response: {data['reply'][:100]}...")
            return True
        else:
            print(f"❌ Simple chat failed: {response.status_code}")
            print(f"Response: {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"❌ Simple chat error: {e}")
        return False

def test_advanced_chat():
    """Test the advanced chat endpoint with conversation history"""
    print("\n🤖 Testing advanced chat...")
    try:
        conversation_history = [
            {"role": "user", "content": "Hi there!"},
            {"role": "assistant", "content": "Hello! How can I help you today?"}
        ]
        
        response = requests.post(
            f"{BASE_URL}/chat",
            json={
                "message": "I need help with my vehicle",
                "conversation_history": conversation_history,
                "temperature": 0.7,
                "max_tokens": 500
            },
            timeout=30
        )
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Advanced chat response: {data['reply'][:100]}...")
            print(f"📊 Tokens used: {data.get('tokens_used', 'N/A')}")
            return True
        else:
            print(f"❌ Advanced chat failed: {response.status_code}")
            print(f"Response: {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"❌ Advanced chat error: {e}")
        return False

def test_models_endpoint():
    """Test the models endpoint"""
    print("\n📋 Testing models endpoint...")
    try:
        response = requests.get(f"{BASE_URL}/models", timeout=10)
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Models endpoint working: {len(data.get('models', []))} models available")
            return True
        else:
            print(f"❌ Models endpoint failed: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"❌ Models endpoint error: {e}")
        return False

def test_automotive_scenarios():
    """Test automotive-specific scenarios"""
    print("\n🚗 Testing automotive scenarios...")
    
    automotive_tests = [
        "I need to schedule an oil change for my Honda Civic",
        "What's included in a brake inspection?",
        "How long does a transmission service take?",
        "Can you help me reschedule my appointment?",
        "What are your service hours?"
    ]
    
    success_count = 0
    for i, message in enumerate(automotive_tests, 1):
        try:
            print(f"  Test {i}/5: {message[:50]}...")
            response = requests.post(
                f"{BASE_URL}/chat/simple",
                json={"message": message},
                timeout=30
            )
            
            if response.status_code == 200:
                data = response.json()
                print(f"    ✅ Response: {data['reply'][:80]}...")
                success_count += 1
            else:
                print(f"    ❌ Failed: {response.status_code}")
                
        except requests.exceptions.RequestException as e:
            print(f"    ❌ Error: {e}")
        
        time.sleep(1)  # Rate limiting
    
    print(f"\n📊 Automotive tests: {success_count}/{len(automotive_tests)} passed")
    return success_count == len(automotive_tests)

def main():
    """Run all tests"""
    print("🧪 GearSync Chatbot Service Test Suite")
    print("=" * 50)
    
    # Wait for service to be ready
    print("⏳ Waiting for service to be ready...")
    time.sleep(2)
    
    # Run tests
    tests = [
        ("Health Check", test_health_check),
        ("Simple Chat", test_simple_chat),
        ("Advanced Chat", test_advanced_chat),
        ("Models Endpoint", test_models_endpoint),
        ("Automotive Scenarios", test_automotive_scenarios)
    ]
    
    passed = 0
    total = len(tests)
    
    for test_name, test_func in tests:
        try:
            if test_func():
                passed += 1
        except Exception as e:
            print(f"❌ {test_name} crashed: {e}")
    
    print("\n" + "=" * 50)
    print(f"📊 Test Results: {passed}/{total} tests passed")
    
    if passed == total:
        print("🎉 All tests passed! The chatbot service is working correctly.")
        sys.exit(0)
    else:
        print("⚠️  Some tests failed. Please check the service configuration.")
        sys.exit(1)

if __name__ == "__main__":
    main()
