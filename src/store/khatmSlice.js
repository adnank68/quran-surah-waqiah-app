// Redux store configuration
import { createSlice, configureStore } from '@reduxjs/toolkit';

const initialState = {
  khatmData: null,
  settings: {},
  loading: false,
  error: null,
};

const khatmSlice = createSlice({
  name: 'khatm',
  initialState,
  reducers: {
    setKhatmData: (state, action) => {
      state.khatmData = action.payload;
    },
    updateSettings: (state, action) => {
      state.settings = { ...state.settings, ...action.payload };
    },
    markDayCompleted: (state, action) => {
      if (state.khatmData && state.khatmData.days) {
        const day = state.khatmData.days.find(d => d.id === action.payload);
        if (day) {
          day.completed = true;
        }
      }
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
    setError: (state, action) => {
      state.error = action.payload;
    },
  },
});

export const {
  setKhatmData,
  updateSettings,
  markDayCompleted,
  setLoading,
  setError,
} = khatmSlice.actions;

export const store = configureStore({
  reducer: {
    khatm: khatmSlice.reducer,
  },
});

export default khatmSlice.reducer;
