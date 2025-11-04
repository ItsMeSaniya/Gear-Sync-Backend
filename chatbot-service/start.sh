#!/bin/bash

# GearSync Chatbot Service Startup Script

echo "🚀 Starting GearSync Chatbot Service..."

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "❌ Error: .env file not found!"
    echo "Please create a .env file with your Google AI Studio API key:"
    echo ""
    echo "GOOGLE_AI_API_KEY=your_actual_api_key_here"
    echo "HOST=0.0.0.0"
    echo "PORT=8000"
    echo "DEBUG=True"
    echo ""
    exit 1
fi

# Check if virtual environment exists
if [ ! -d "venv" ]; then
    echo "📦 Creating virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
echo "🔧 Activating virtual environment..."
source venv/bin/activate

# Install dependencies
echo "📥 Installing dependencies..."
pip install -r requirements.txt

# Check if API key is set
if ! grep -q "GOOGLE_AI_API_KEY=your_actual_api_key_here" .env; then
    echo "✅ API key configuration found"
else
    echo "⚠️  Warning: Please update your API key in the .env file"
fi

# Start the service
echo "🌟 Starting chatbot service..."
echo "📖 API Documentation: http://localhost:8000/docs"
echo "🔍 Health Check: http://localhost:8000/health"
echo ""

python main.py
