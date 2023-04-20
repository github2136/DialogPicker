时间、日期、多级选择库
[![](https://jitpack.io/v/github2136/DialogPicker.svg)](https://jitpack.io/#github2136/DialogPicker)
使用该库还需要引用以下库  

```groovy
//Android库
implementation "androidx.appcompat:appcompat:1.3.1"
implementation "androidx.recyclerview:recyclerview:1.2.1"
implementation "com.google.android.material:material:1.4.0"
implementation "androidx.core:core-ktx:1.5.0"
```

* `DataLevelPickerDialog`：多级联动实体类实现`IDataLevel`接口，具体操作查看`MainActivity.kt`
* `DatePickerDialog`：日期单选
* `DateRangPickerDialog`：日期范围选择
* `DateTimePickerDialog`：日期时间单选
* `DateTimeRangPickerDialog`：日期时间范围选择
* `TimePickerDialog`：时间单选
* `TimePickerDialog`：时间范围选择