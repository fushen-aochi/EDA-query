import axios from 'axios';

const api = axios.create({
  baseURL: '/api'
});

export async function sendMessage(payload) {
  const { data } = await api.post('/chat/send', payload);
  return data;
}

export async function fetchSessions() {
  const { data } = await api.get('/chat/sessions');
  return data;
}

export async function fetchMessages(sessionId) {
  const { data } = await api.get(`/chat/sessions/${sessionId}/messages`);
  return data;
}
