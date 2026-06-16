const fs = require('fs');
const path = require('path');

const basePath = 'C:\\Users\\LENOVO1\\Documents\\PROJECT SKRIPSI\\manajement_magang\\src\\main\\java\\com\\bsi\\manajement_magang';

function replaceInFile(relativePath, search, replace) {
    const fullPath = path.join(basePath, relativePath);
    let content = fs.readFileSync(fullPath, 'utf8');
    if (typeof search === 'string') {
        content = content.replace(search, replace);
    } else {
        content = content.replace(search, replace);
    }
    fs.writeFileSync(fullPath, content, 'utf8');
    console.log(`Patched ${relativePath}`);
}

replaceInFile('modules\\dashboard_mentor\\repository\\DashboardMentorRepository.java',
    /rs\.getString\("gender"\),/g,
    'rs.getString("gender") != null ? Gender.fromString(rs.getString("gender")) : null,'
);

// DashboardMentorRepository.java:226
replaceInFile('modules\\dashboard_mentor\\repository\\DashboardMentorRepository.java',
    /public Optional<SearchStudentResponse> findStudentByNim\(String nim\) \{\s*return jdbcTemplate\.query\(sql, new MapSqlParameterSource\("nim", nim\), \(rs, rowNum\) -> new SearchStudentResponse\([\s\S]*?\)\)\.stream\(\)\.findFirst\(\);/m,
    `public Optional<SearchStudentResponse> findStudentByNim(String nim) {
        return jdbcTemplate.query(sql, new MapSqlParameterSource("nim", nim), (rs, rowNum) -> new SearchStudentResponse(
            rs.getString("nim"),
            rs.getString("nama"),
            rs.getString("universitas"),
            rs.getString("gender") != null ? Gender.fromString(rs.getString("gender")) : null,
            rs.getString("no_hp"),
            rs.getString("email"),
            rs.getObject("user_id", java.util.UUID.class)
        )).stream().findFirst();`
);

replaceInFile('modules\\dashboard_mentor\\service\\impl\\DashboardMentorServiceImpl.java',
    /req\.gender\(\),/g,
    'req.gender() != null ? req.gender().getValue() : null,'
);

replaceInFile('modules\\dashboard_mentor\\service\\impl\\DashboardMentorServiceImpl.java',
    /\(String\) row\.get\("gender"\),/g,
    'row.get("gender") != null ? Gender.fromString((String) row.get("gender")) : null,'
);

replaceInFile('modules\\data_absensi\\repository\\DataAbsensiRepository.java',
    /rs\.getString\("status"\),/g,
    'rs.getString("status") != null ? StatusAbsensi.fromString(rs.getString("status")) : null,'
);

replaceInFile('modules\\data_kegiatan\\repository\\DataKegiatanRepository.java',
    /rs\.getString\("status"\)\s*\)/g,
    'rs.getString("status") != null ? StatusKegiatan.fromString(rs.getString("status")) : null\n            )'
);

replaceInFile('modules\\data_mahasiswa\\repository\\DataMahasiswaRepository.java',
    /rs\.getString\("gender"\),/g,
    'rs.getString("gender") != null ? Gender.fromString(rs.getString("gender")) : null,'
);

replaceInFile('modules\\data_mahasiswa\\service\\impl\\DataMahasiswaServiceImpl.java',
    /req\.gender\(\),/g,
    'req.gender() != null ? req.gender().getValue() : null,'
);

replaceInFile('modules\\data_mahasiswa\\service\\impl\\DataMahasiswaServiceImpl.java',
    /String resolvedGender = req\.gender\(\) != null \? req\.gender\(\) : student\.gender\(\);/g,
    'Gender resolvedGenderEnum = req.gender() != null ? req.gender() : student.gender();\n        String resolvedGender = resolvedGenderEnum != null ? resolvedGenderEnum.getValue() : null;'
);

replaceInFile('modules\\data_mahasiswa\\service\\impl\\DataMahasiswaServiceImpl.java',
    /String status = req\.periode\(\)\.status\(\);/g,
    'String status = req.periode().status() != null ? req.periode().status().getValue() : null;'
);

replaceInFile('modules\\iam\\service\\impl\\IamServiceImpl.java',
    /gender = req\.gender\(\);/g,
    'gender = req.gender() != null ? req.gender().getValue() : null;'
);

// IamServiceImpl.java constructor mappings
replaceInFile('modules\\iam\\service\\impl\\IamServiceImpl.java',
    /new UserResponse\([\s\S]*?gender,[\s\S]*?\)/gm,
    (match) => match.replace(/\bgender,\s/g, 'gender != null ? Gender.fromString(gender) : null,\n                ')
);

replaceInFile('modules\\iam\\service\\impl\\IamServiceImpl.java',
    /if \(req\.gender\(\) != null\) m\.setGender\(req\.gender\(\)\);/g,
    'if (req.gender() != null) m.setGender(req.gender().getValue());'
);

replaceInFile('modules\\data_absensi\\service\\impl\\DataAbsensiServiceImpl.java',
    /record\.status\(\)\.toUpperCase\(\)/g,
    'record.status().getValue().toUpperCase()'
);

console.log("All patches applied.");
