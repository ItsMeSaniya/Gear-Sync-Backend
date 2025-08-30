from fastapi import FastAPI
from pydantic import BaseModel
import openai
import os

# Create FastAPI app instance
app = FastAPI()

openai.api_key = os.getenv("OPENAI_API_KEY")

# Request model
class ChatRequest(BaseModel):
    message: str

# Chat endpoint
@app.post("/chat")
async def chat(request: ChatRequest):
    response = openai.ChatCompletion.create(
        model="gpt-4",
        messages=[{"role": "user", "content": request.message}],
        max_tokens=150
    )
    return {"reply": response.choices[0].message.content}