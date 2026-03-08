package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new com.warbler.feature.weather.test.DataBinderMapperImpl());
  }
}
