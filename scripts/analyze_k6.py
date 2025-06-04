import json
import csv
import matplotlib.pyplot as plt
from datetime import datetime
import os

# booking 또는 token으로 변경해서 사용
SUMMARY_JSON_PATH = './booking/peakTestSummary.json'
CSV_OUTPUT_PATH = './booking/peakTestSummary.csv'
CHART_OUTPUT_PATH = 'booking/peakTest_response_chart.png'

# JSON → CSV 변환
def convert_json_to_csv(json_path, csv_path):
    with open(json_path, 'r') as f:
        data = json.load(f)

    metrics = data.get('metrics', {})
    with open(csv_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['metric', 'avg', 'min', 'max', 'p(90)', 'p(95)'])

        for name, values in metrics.items():
            if 'values' in values:
                v = values['values']
                writer.writerow([
                    name,
                    v.get('avg', ''),
                    v.get('min', ''),
                    v.get('max', ''),
                    v.get('p(90)', ''),
                    v.get('p(95)', '')
                ])
    print(f"[✅] CSV 저장 완료: {csv_path}")

# 응답 시간 시각화 (http_req_duration 기준)
def plot_response_times(json_path, output_path):
    with open(json_path, 'r') as f:
        data = json.load(f)

    duration = data['metrics'].get('http_req_duration', {})


    if not duration:
        print("[⚠️] http_req_duration 데이터 없음.")
        return

    labels = ['min', 'avg', 'p(90)', 'p(95)', 'max']
    values = [
        duration.get('min', 0),
        duration.get('avg', 0),
        duration.get('p(90)', 0),
        duration.get('p(95)', 0),
        duration.get('max', 0),
    ]

    plt.figure(figsize=(10, 5))
    plt.bar(labels, values, color='skyblue')
    plt.title('HTTP Request Duration (ms)')
    plt.ylabel('Time (ms)')
    plt.grid(True)
    plt.savefig(output_path)
    plt.close()

    print(f"[✅] 응답 시간 그래프 저장 완료: {output_path}")

if __name__ == "__main__":
    if not os.path.exists(SUMMARY_JSON_PATH):
        print(f"[❌] summary.json 파일이 존재하지 않습니다: {SUMMARY_JSON_PATH}")
    else:
        convert_json_to_csv(SUMMARY_JSON_PATH, CSV_OUTPUT_PATH)
        plot_response_times(SUMMARY_JSON_PATH, CHART_OUTPUT_PATH)
