const fs = require('fs');

function addImports(filePath, classes) {
    let content = fs.readFileSync(filePath, 'utf8');
    let importsToAdd = classes.map(c => `import com.bsi.manajement_magang.enums.${c};`).join('\n');
    if (!content.includes(`import com.bsi.manajement_magang.enums.${classes[0]};`)) {
        content = content.replace(/(package\s+[^;]+;)/, `$1\n\n${importsToAdd}`);
        fs.writeFileSync(filePath, content, 'utf8');
        console.log(`Added imports to ${filePath}`);
    }
}

const basePath = 'C:\\Users\\LENOVO1\\Documents\\PROJECT SKRIPSI\\manajement_magang\\src\\main\\java\\com\\bsi\\manajement_magang\\modules';

addImports(`${basePath}\\dashboard_mentor\\repository\\DashboardMentorRepository.java`, ['Gender', 'StatusPeriode']);
addImports(`${basePath}\\dashboard_mentor\\service\\impl\\DashboardMentorServiceImpl.java`, ['Gender', 'StatusPeriode']);
addImports(`${basePath}\\data_absensi\\repository\\DataAbsensiRepository.java`, ['StatusAbsensi']);
addImports(`${basePath}\\data_kegiatan\\repository\\DataKegiatanRepository.java`, ['StatusKegiatan']);
addImports(`${basePath}\\data_mahasiswa\\repository\\DataMahasiswaRepository.java`, ['Gender', 'StatusPeriode']);
addImports(`${basePath}\\data_mahasiswa\\service\\impl\\DataMahasiswaServiceImpl.java`, ['Gender']);
addImports(`${basePath}\\iam\\service\\impl\\IamServiceImpl.java`, ['Gender']);

// Fix DataMahasiswaRepository.java:333
const dmRepo = `${basePath}\\data_mahasiswa\\repository\\DataMahasiswaRepository.java`;
let dmContent = fs.readFileSync(dmRepo, 'utf8');
dmContent = dmContent.replace(/rs\.getString\("status_periode"\),/g, 'rs.getString("status_periode") != null ? StatusPeriode.fromString(rs.getString("status_periode")) : null,');
fs.writeFileSync(dmRepo, dmContent, 'utf8');
console.log('Fixed DataMahasiswaRepository status_periode');
