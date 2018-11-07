# 자바 삼각측량 관련 모음

---------------------------------------

### Package : Util_1
REF Github : https://github.com/lemmingapex/Trilateration

#### 참고내용
* 4 개 또는 그 이상의 지점을 통해 비콘의 위치를 찾는 방법.
* Apache Commons Math Lib를 이용. 즉, 자바에서만 가능한 방법

#### Params
* Position : 관측 지점 값.
* Distance : 관측 지점과 비콘과의 거리.

```
# Input : 
double[][] positions = new double[][] { { 5.0, -6.0 }, { 13.0, -15.0 }, { 21.0, -3.0 }
                                        , { 12.4, -21.2 }, { 12.4, -1.2 } };
double[] distances = new double[] { 8.06, 13.97, 23.32, 15.31, 24.26 };
```


#### Using
* Calculate and return Jacobian function Actually return initialized function
```
//Jacobian matrix, [i][j] at
J[i][0] = delta_[(x0-xi)^2 + (y0-yi)^2 - ri^2]/delta_[x0] 
//at
J[i][1] = delta_[(x0-xi)^2 + (y0-yi)^2 - ri^2]/delta_[y0] 
//partial derivative with respect to the parameters passed to value() method

double[][] jacobian = new double[distances.length][pointArray.length];
for (int i = 0; i < jacobian.length; i++) {
  for (int j = 0; j < pointArray.length; j++) {
    jacobian[i][j] = 2 * pointArray[j] - 2 * positions[i][j];
  }
}
```

* target values at optimal point in least square equation
```
// input
double[] pointArray = point.toArray();

// output
double[] resultPoint = new double[this.distances.length];

// compute least squares
for (int i = 0; i < resultPoint.length; i++) {
  resultPoint[i] = 0.0;
  // calculate sum, add to overall
  for (int j = 0; j < pointArray.length; j++) {
    resultPoint[i] += (pointArray[j] - this.getPositions()[i][j]) * (pointArray[j] - this.getPositions()[i][j]);
  }
  resultPoint[i] -= (this.getDistances()[i]) * (this.getDistances()[i]);
}

RealMatrix jacobian = jacobian(point);
return new Pair<RealVector, RealMatrix>(new ArrayRealVector(resultPoint), jacobian);
```

#### Result
* centroid : 비콘 지점 x, y position 값
* standardDeviation : 표준편차
* covarianceMatrix : 공분산 행렬

```
# Output : 
centroid : { -1.0118905367564652, -13.90997464584826 } 
Standard : {0.2900391636; 0.4190526507}
ConvarianceMatrix : BlockRealMatrix{{0.0841227164,-0.0481968747},{-0.0481968747,0.1756051241}}
```

---------------------------------------

### Package : Util_2
* REF : https://everything2.com/title/Triangulate
* REF2 : https://stackoverflow.com/questions/20332856/triangulate-example-for-ibeacons

#### 참고내용
* 3 개의 지점을 알고 있을 때, 이용할 수 있는 방법.
* Lib를 이용하지 않고 알고리즘을 이용. 즉, 어디서나 쓸 수 있음.

#### Params
* Location : 세 지점의 경도, 위도 값이 필요
* Distance : 세 지점과 비콘과의 거리 값이 필요.
```
## Input
Device_A : 37.45096, 127.12709, 3.06 (m)
Device_B : 37.450962, 127.127106, 4.69 (m)
Device_C : 37.450996, 127.12711, 3.52 (m)
```

#### Result
* 최소 위도, 경도 좌표에서의 비콘까지의 거리(m) 값이 출력
* 최소 위도, 경도를 기점으로 하여 O(0.0)로 설정, 각 지점 A, B, C는 최소 기점 O를 기준으로 하여 거리 값으로 (x, y)로 변환
* Width : 삼각형 가로 길이
* Height : 삼각형 높이

```
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
  
