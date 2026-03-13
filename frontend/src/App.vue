<template>
  <div class="page-wrap">
    <div class="chat-shell">
      <aside class="session-pane">
        <div class="pane-header">
          <h1>EDA课程助教</h1>
          <button @click="startNewChat">新建</button>
        </div>

        <div class="session-list">
          <button
            v-for="item in sessions"
            :key="item.sessionId"
            :class="['session-item', { active: item.sessionId === currentSessionId }]"
            @click="openSession(item.sessionId)"
          >
            <div class="title">{{ item.title || '新会话' }}</div>
            <div class="time">{{ formatTime(item.updatedAt) }}</div>
          </button>

          <p v-if="sessions.length === 0" class="empty">还没有聊天记录</p>
        </div>
      </aside>

      <main class="chat-pane">
        <header class="chat-header">
          <h2>{{ currentTitle }}</h2>
          <span class="hint">优先使用教师SQL数据，缺失时调用DeepSeek</span>
        </header>

        <section class="messages" ref="messageBox">
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            :class="['msg-row', msg.role === 'user' ? 'me' : 'ai']"
          >
            <div class="bubble">
              <div
                v-if="msg.role === 'assistant'"
                class="content md-content"
                v-html="renderMarkdown(msg.content)"
              />
              <div v-else class="content">{{ msg.content }}</div>
              <div class="source" v-if="msg.role === 'assistant'">
                来源: {{ msg.source === 'teacher-sql' ? '教师SQL' : 'DeepSeek' }}
              </div>
            </div>
          </div>

          <div class="msg-row ai" v-if="loading">
            <div class="bubble typing">正在思考中...</div>
          </div>
        </section>

        <footer class="input-area">
          <textarea
            v-model="draft"
            placeholder="请输入你的课程问题，Enter发送，Shift+Enter换行"
            @keydown.enter.exact.prevent="submit"
            @keydown.enter.shift.exact.stop
          />
          <button :disabled="loading || !draft.trim()" @click="submit">发送</button>
        </footer>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue';
import DOMPurify from 'dompurify';
import { marked } from 'marked';
import { fetchMessages, fetchSessions, sendMessage } from './api';

const sessions = ref([]);
const messages = ref([]);
const currentSessionId = ref('');
const draft = ref('');
const loading = ref(false);
const messageBox = ref(null);

const currentTitle = computed(() => {
  const hit = sessions.value.find((x) => x.sessionId === currentSessionId.value);
  return hit ? hit.title : '新会话';
});

marked.setOptions({
  breaks: true,
  gfm: true
});

function renderMarkdown(content) {
  const raw = marked.parse(content || '');
  return DOMPurify.sanitize(raw);
}

function formatTime(val) {
  if (!val) {
    return '';
  }
  const d = new Date(val);
  return `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
}

async function refreshSessions() {
  sessions.value = await fetchSessions();
}

async function openSession(sessionId) {
  currentSessionId.value = sessionId;
  messages.value = await fetchMessages(sessionId);
  await scrollBottom();
}

function startNewChat() {
  currentSessionId.value = '';
  messages.value = [];
}

async function submit() {
  const text = draft.value.trim();
  if (!text || loading.value) {
    return;
  }

  loading.value = true;
  const userMsg = {
    role: 'user',
    content: text,
    source: 'user'
  };
  messages.value.push(userMsg);
  draft.value = '';
  await scrollBottom();

  try {
    const data = await sendMessage({
      sessionId: currentSessionId.value,
      message: text
    });

    currentSessionId.value = data.sessionId;
    messages.value.push({
      role: 'assistant',
      content: data.answer,
      source: data.source
    });

    await refreshSessions();
    await scrollBottom();
  } catch (err) {
    const fallback = '请求失败，请检查后端服务和API配置。';
    const message = err?.response?.data?.message || err?.message || fallback;
    messages.value.push({
      role: 'assistant',
      content: `错误: ${message}`,
      source: 'deepseek'
    });
    await scrollBottom();
  } finally {
    loading.value = false;
  }
}

async function scrollBottom() {
  await nextTick();
  if (messageBox.value) {
    messageBox.value.scrollTop = messageBox.value.scrollHeight;
  }
}

onMounted(async () => {
  await refreshSessions();
  if (sessions.value.length > 0) {
    await openSession(sessions.value[0].sessionId);
  }
});
</script>
