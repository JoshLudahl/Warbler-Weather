package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new com.warbler.feature.location.test.DataBinderMapperImpl());
  }
}
