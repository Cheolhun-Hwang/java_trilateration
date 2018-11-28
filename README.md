# 자바 삼각측량 관련 모음

--------------------------------------
## Infomation

### 환경 정의
1. 각 스캐너의 좌표는 "절대 좌표"이다.
2. API 방식이 아닌 "모듈" 방식의 호출로써 동작한다.
3. 각 스캐너 디바이스의 좌표값과 비콘까지의 RSSI, TX 값을 받는다.

### IBeacon Infomation
원문 : https://arsviator.blogspot.com/2015/02/beacon.html

* 애플이 iOS7에서 iBeacon 기술을 소개했을 때, 애플의 문서는 거리 추정치를 직접 사용하지 않는걸 권장했다. 비이컨 범위 정보를 제공하는 CLBeacon 클래스는 비이컨과의 거리 추정치를 미터 단위로 제공하는 필드를 가지고 있다. 하지만 이 속성을 distance라고 부르는 대신 애플은 accuracy라고 이름붙였다. 권장하는 사용법은 이 값을 여러 비이컨들 중에 어느것이 가장 가까운가 비교하는 용도로만 사용하는 것이다. 또한 CLBeacon 클래스는 proximity라는 속성을 제공해 거리 추정치를 “immediate”, “near”, “far”로 그룹핑 한다. 이 각 그룹의 정의는 명확하지 않지만, 실험 결과 0.5미터 이내의 거리는 “immediate”, 0.5~3m 정도는 “near”, 그 이상은 “far”로 구분한다고 볼 수 있다.
* 이런 내용이 비이컨을 사용해 직접적으로 거리를 측정할 수 없다는걸 의미하지는 않는다. 단지 비이컨의 동작 원리와 결과값의 품질에 어떤 한계가 있는지를 먼저 이해할 필요가 있다는걸 의미한다.
* 모바일 디바이스는 비이컨의 신호레벨을 레퍼런스 신호레벨과 비교함으로서 비이컨과의 거리를 추정할 수 있다. 비이컨이 adv한 패킷이 수신될 때 마다 블루투스 칩은 비이컨의 신호레벨 측정값을 RSSI로 제공한다. 각 비이컨 전송은 위에서 언급한 calibration 값을 포함하고 있기 때문에, 실제 시그널 레벨을 1머터에서 기대되는 시그널 레벨과 비교해서 거리를 추정할 수 있다. 예를들어 비이컨 adv 패킷이 -65 dBm 시그널 레벨로 수신되었고 송신기의 출력 calibration값은 -59 dBm이라고 해 보자. -65 dBm은 -59dBm보다 약한 신호레벨이므로, 즉 비이컨은 1미터보다 먼 거리에 있을 가능성이 크다는걸 의미한다.
* 거리를 추정하기 위해 이 두 숫자를 공식에 집어넣을 수 있다. 아래 공식은 Android Beacon Library에 사용한 것이다. 공식의 3개의 상수(0.89976, 7.7095, 0.111)는 여러 정해진 거리에서 넥서스4를 사용해 측정한 신호 세기에 기반해 best fit으로 계산한 값이다. 

```
// txPower, Rssi 값을 통한 거리 추정
protected static double calculateAccuracy(int txPower, double rssi) {
	if (rssi == 0) {
		return -1.0; // if we cannot determine accuracy, return -1.
	}
	double ratio = rssi*1.0/txPower;
	if (ratio < 1.0) {
		return Math.pow(ratio,10);
	}
	else {
		double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;	
		Log.d(TAG, " avg rssi: "+rssi+" accuracy: "+accuracy);
		return accuracy;
	}
}
  
// 거리 추정치에 대한 지수 변환 ( 구분 : Immediate, near, far )
protected static int calculateProximity(double accuracy) {
  if (accuracy < 0) {
    return PROXIMITY_UNKNOWN;	 
    // is this correct?  
    // does proximity only show unknown when accuracy is negative?  
    // I have seen cases where it returns unknown when accuracy is -1;
    }
	if (accuracy < 0.5 ) {
		return IBeacon.PROXIMITY_IMMEDIATE;
	}
	if (accuracy <= 3.0) { 
		return IBeacon.PROXIMITY_NEAR;
	}
	// if it is > 3.0 meters, call it far
	return IBeacon.PROXIMITY_FAR;
}

```


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
  
  
