package bo.sipqr.configuracion;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@MapperScan("bo.sipqr.repositorio.mapeador")
public class ConfiguracionMybatis {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapeadores/*.xml"));
        bean.setTypeAliasesPackage("bo.sipqr.modelo");
        org.apache.ibatis.session.Configuration configuracion = new org.apache.ibatis.session.Configuration();
        configuracion.setMapUnderscoreToCamelCase(true);
        configuracion.setDefaultFetchSize(100);
        configuracion.setDefaultStatementTimeout(30);
        bean.setConfiguration(configuracion);
        return bean.getObject();
    }
}
