import api from "../axios";
import type {
  DailyQuestionAnswerResult,
  FeedItem,
  SubmitDailyQuestionAnswerRequest,
} from "../../data/types";

export const DailyQuestionService = {
  async getNextQuestion(): Promise<FeedItem.DAILYQUESTIONITEM> {
    const res = await api.get<FeedItem.DAILYQUESTIONITEM>("/daily-questions/next");
    return res.data;
  },

  async submitAnswer(payload: SubmitDailyQuestionAnswerRequest): Promise<DailyQuestionAnswerResult> {
    const res = await api.post<DailyQuestionAnswerResult>("/daily-questions/answer", payload);
    return res.data;
  },
};
