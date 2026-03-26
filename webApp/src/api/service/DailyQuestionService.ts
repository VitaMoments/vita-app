import api from "../axios";
import type {
  DailyQuestion,
  DailyQuestionAnswerResult,
  SubmitDailyQuestionAnswerRequest,
} from "../../data/types";

export const DailyQuestionService = {
  async getNextQuestion(): Promise<DailyQuestion> {
    const res = await api.get<DailyQuestion>("/daily-questions/next");
    return res.data;
  },

  async submitAnswer(payload: SubmitDailyQuestionAnswerRequest): Promise<DailyQuestionAnswerResult> {
    const res = await api.post<DailyQuestionAnswerResult>("/daily-questions/answer", payload);
    return res.data;
  },
};
