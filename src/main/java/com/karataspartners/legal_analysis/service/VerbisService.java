package com.karataspartners.legal_analysis.service;

import com.karataspartners.legal_analysis.util.VerbisChecker;
import org.springframework.stereotype.Service;

@Service
public class VerbisService {
    private final VerbisChecker verbisChecker;

    public VerbisService(VerbisChecker verbisChecker) {
        this.verbisChecker = verbisChecker;
    }

    public String checkRecords(String url) {
        String verbisResult = verbisChecker.checkVerbisRecord(url);
        String etbisResult = verbisChecker.checkETBISRecord(url);
        //String iysResult = verbisChecker.checkIYSRecord(url);

        return verbisResult + "\n" + etbisResult +"\n";
}
}