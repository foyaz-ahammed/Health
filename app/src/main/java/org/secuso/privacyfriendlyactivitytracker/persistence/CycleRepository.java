package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * 생리관련 repository
 */
public class CycleRepository {
    public OvulationDao mOvulationDao;
    public SymptomDao mSymptomDao;
    public CycleLengthDao mCycleLengthDao;

    public CycleRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mOvulationDao = db.OvulationDao();
        mSymptomDao = db.SymptomDao();
        mCycleLengthDao = db.CycleLengthDao();
    }

    /**
     * 주어진 날자에 해당한 생리자료 얻는 함수
     * @param date 날자
     * @return 생리자료목록
     */
    public LiveData<List<Ovulation>> getOvulation(int date) {
        return mOvulationDao.getOvulationByDate(date);
    }

    /**
     * 주어진 날자의 이전 생리자료를 얻는 함수
     * @param date 날자
     * @return 생리자료
     */
    public Ovulation getPrevOvulation(int date) {
        return mOvulationDao.getPrevOvulation(date);
    }

    /**
     * 주어진 날자의 다음 생리자료를 얻는 함수
     * @param date 날자
     * @return 생리자료
     */
    public Ovulation getNextOvulation(int date) {
        return mOvulationDao.getNextOvulation(date);
    }

    /**
     * 마지막생리자료를 얻는 함수
     * @return 마지막생리자료
     */
    public Ovulation getLastOvulation() {
        return mOvulationDao.getLastOvulation();
    }

    /**
     * 주어진 날자에 준하여 상태표시를 위한 생리자료목록을 얻는 함수
     * @param date 날자
     * @return 생리자료목록
     */
    public LiveData<List<Ovulation>> getOvulationForStatus(int date) {
        return mOvulationDao.getOvulationForStatus(date);
    }

    /**
     * 주어진 날자에 준하여 총날자를 현시하기 위한 생리자료 얻는 함수
     * @param date 날자
     * @return 생리자료목록
     */
    public LiveData<List<Ovulation>> getOvulationForTotal(int date) {
        return mOvulationDao.getOvulationForTotal(date);
    }

    /**
     * 통계자료현시를 위한 생리자료목록 얻는 함수
     * @return 생리자료목록
     */
    public LiveData<List<Ovulation>> getOvulationForStats() {
        return mOvulationDao.getOvulationForStats();
    }

    /**
     * 주어진 날자에 준하여 신체적증상자료 얻는 함수
     * @param date 날자
     * @return 신체적증상 자료
     */
    public LiveData<Symptom> getSymptom(int date) {
        return mSymptomDao.getSymptomByDate(date);
    }

    /**
     * 월간 신체적증상자료목록을 얻는 함수
     * @param start 월의 시작시간
     * @param end 월의 마감시간
     * @return 신체적증상 자료목록
     */
    public LiveData<List<Symptom>> getMonthSymptom(int start, int end) {
        return mSymptomDao.getMonthData(start, end);
    }

    /**
     * 주어진 월기간에 기초하여 3개월간의 생리자료목록을 얻는 함수
     * @param start 월의 시작시간
     * @param end 월의 마감시간
     * @return 생리자료목록
     */
    public LiveData<List<Ovulation>> getMonthOvulation(int start, int end) {
        return mOvulationDao.getMonthData(start, end);
    }

    /**
     * 생리자료 추가 및 갱신함수
     * @param ovulation 새로 추가 및 갱신될 생리자료
     */
    public void insertOrUpdateOvulation(Ovulation ovulation) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mOvulationDao.insertOrUpdate(ovulation);
            }
        });
    }

    /**
     * 생리자료 삭제함수
     * @param id 삭제될 생리자료의 id
     */
    public void deleteOvulation(int id) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mOvulationDao.deleteById(id);
            }
        });
    }

    /**
     * 예상자료를 삭제하는 함수
     */
    public void deletePredictOvulation() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mOvulationDao.deletePredictData();
            }
        });
    }

    /**
     * 생리자료개수를 얻는 함수
     * @return 생리자료개수
     */
    public int getOvulationCount() {
        return mOvulationDao.getOvulationCount();
    }

    /**
     * 신체적증상자료 추가 및 갱신하는 함수
     * @param symptom 새로 추가 및 갱신될 신체적증상자료
     */
    public void insertOrUpdateSymptom(Symptom symptom) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mSymptomDao.insertOrUpdate(symptom);
            }
        });
    }

    /**
     * 신체적증상자료 삭제 함수
     * @param id 삭제될 신체적증상자료의 id
     */
    public void deleteSymptom(int id) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mSymptomDao.deleteById(id);
            }
        });
    }

    /**
     * 생리주기길이 얻는 함수 (LiveData)
     * @return 생리주기길이
     */
    public LiveData<CycleLength> getCycleLengthLiveData() {
        return mCycleLengthDao.getCycleLengthLiveData();
    }

    /**
     * 생리주기길이 얻는 함수
     * @return 생리주기길이
     */
    public CycleLength getCycleLengthData() {
        return mCycleLengthDao.getCycleLengthData();
    }

    /**
     * 생리주기길이 추가 및 갱신하는 함수
     * @param cycleLength 생리주기길이
     */
    public void insertOrUpdateLength(CycleLength cycleLength) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mCycleLengthDao.insertOrUpdate(cycleLength);
            }
        });
    }
}
