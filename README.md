# 자바 삼각측량 관련 모음

### Util_1
REF Github : https://github.com/lemmingapex/Trilateration

#### 참고내용
* 4 개 또는 그 이상의 지점을 통해 중심점을 찾는 방법.
* Apache Commons Math Lib를 이용. 즉, 자바에서만 가능한 방법


### Util_2
* REF : https://everything2.com/title/Triangulate
* REF2 : https://stackoverflow.com/questions/20332856/triangulate-example-for-ibeacons

#### 참고내용
* 3 개의 지점을 알고 있을 때, 이용할 수 있는 방법.
* Lib를 이용하지 않고 알고리즘을 이용. 즉, 어디서나 쓸 수 있음.

#### Params
* Location : 세 지점의 경도, 위도 값이 필요
* Distance : 세 지점과 비콘과의 거리 값이 필요.

#### Result
* 최소 위도, 경도 좌표에서의 비콘까지의 거리(m) 값이 출력
* 최소 위도, 경도를 기점으로 하여 O(0.0)로 설정, 각 지점 A, B, C는 최소 기점 O를 기준으로 하여 거리 값으로 (x, y)로 변환
* Width : 삼각형 가로 길이
* Height : 삼각형 높이

```
## Input
Device_A : 37.45096, 127.12709, 3.06 (m)
Device_B : 37.450962, 127.127106, 4.69 (m)
Device_C : 37.450996, 127.12711, 3.52 (m)

## Traingle info
Width : 3.490658505096642E-7
Height : 6.283185307437772E-7
Location A Point : 0.0 / 0.0
Location B Point : 3.4906584951755934E-8 / 2.792526803581261E-7
Location C Point : 6.283185307437772E-7 / 3.490658505096642E-7
Location : x = 1.0915123924536066E7 / y = -2.3982794693113983E7
## TARGET TEST...
Location Target Point : 1.7453292513081898E-7 / 0.0
```

#### 문제점 : 
* 실험을 비콘의 오차범위 이내로 진행하였기 때문에, 위의 결과 값은 문제가 있음.
* 위치 값은 자체 제작 안드로이드 애플리케이션을 통해 획득. 단, 안드로이드의 경우 실내에서는 GPS 값은 Network로만 받음.
  즉, 위치에 대한 정확도에 문제가 있음.
  
