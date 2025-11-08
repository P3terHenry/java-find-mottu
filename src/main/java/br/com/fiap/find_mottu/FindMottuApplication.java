package br.com.fiap.find_mottu;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching
@EnableJpaRepositories
@EntityScan
@SpringBootApplication
public class FindMottuApplication {

	public static void main(String[] args) {
		// Carrega .env apenas se existir, sem quebrar em produção
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() // <--- evita exception se .env não existir
				.load();

		// Seta variáveis de ambiente apenas se ainda não estiverem definidas
		setIfAbsent("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
		setIfAbsent("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		setIfAbsent("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
		setIfAbsent("SPRING_DATASOURCE_DRIVER", dotenv.get("SPRING_DATASOURCE_DRIVER"));
		setIfAbsent("SQUARECLOUD_API_KEY", dotenv.get("SQUARECLOUD_API_KEY"));

		SpringApplication.run(FindMottuApplication.class, args);
	}

	private static void setIfAbsent(String key, String value) {
		if (System.getenv(key) == null && value != null) {
			System.setProperty(key, value);
		}
	}
}