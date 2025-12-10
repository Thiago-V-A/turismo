package org.turismo.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.turismo.model.PontoTuristico;
import org.turismo.repository.PontoTuristicoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final PontoTuristicoRepository pontoRepository;

    public String exportarJSON() throws IOException {
        List<PontoTuristico> pontos = pontoRepository.findAll();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pontos);
    }

    public String exportarCSV() throws IOException {
        List<PontoTuristico> pontos = pontoRepository.findAll();
        StringWriter writer = new StringWriter();

        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Nome", "Cidade", "Estado", "País", "Nota Média", "Total Avaliações"));

        for (PontoTuristico ponto : pontos) {
            printer.printRecord(
                    ponto.getId(),
                    ponto.getNome(),
                    ponto.getCidade(),
                    ponto.getEstado(),
                    ponto.getPais(),
                    ponto.getNotaMedia(),
                    ponto.getTotalAvaliacoes()
            );
        }

        printer.flush();
        return writer.toString();
    }

    public String exportarXML() throws IOException {
        List<PontoTuristico> pontos = pontoRepository.findAll();
        XmlMapper mapper = new XmlMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pontos);
    }
}
