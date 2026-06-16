const fs = require('fs');
const path = require('path');

const basePath = 'C:\\Users\\LENOVO1\\Documents\\PROJECT SKRIPSI\\manajement_magang\\src\\main\\java\\com\\bsi\\manajement_magang';

const replacements = [
    [/\\bString gender\\b/g, 'Gender gender'],
    [/\\bString statusAbsensi\\b/g, 'StatusAbsensi statusAbsensi'],
    [/\\bString statusVerifikasi\\b/g, 'StatusVerifikasi statusVerifikasi'],
    [/\\bString statusKegiatan\\b/g, 'StatusKegiatan statusKegiatan'],
    [/\\bString statusPeriode\\b/g, 'StatusPeriode statusPeriode']
];

const moduleStatusMap = {
    'data_absensi': 'StatusAbsensi',
    'data_kegiatan': 'StatusKegiatan',
    'data_mahasiswa': 'StatusPeriode',
    'dashboard_mahasiswa': 'StatusPeriode',
    'dashboard_mentor': 'StatusPeriode',
    'surat_keterangan': 'StatusPeriode',
    'sertifikat': 'StatusPeriode'
};

function processDirectory(dir) {
    if (dir.includes('enums')) return;
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const filePath = path.join(dir, file);
        if (fs.statSync(filePath).isDirectory()) {
            processDirectory(filePath);
        } else if (file.endsWith('.java') && (filePath.includes('\\schema\\request') || filePath.includes('\\schema\\response') || filePath.includes('/schema/request') || filePath.includes('/schema/response'))) {
            let content = fs.readFileSync(filePath, 'utf8');
            const origContent = content;
            
            for (const [pattern, repl] of replacements) {
                content = content.replace(new RegExp(pattern.source.replace(/\\\\/g, '\\'), 'g'), repl);
            }
            
            for (const [module, enumClass] of Object.entries(moduleStatusMap)) {
                if (filePath.includes(`\\modules\\${module}\\`) || filePath.includes(`/modules/${module}/`)) {
                    content = content.replace(/\bString status\b/g, `${enumClass} status`);
                }
            }
            
            if (content !== origContent) {
                const enumsUsed = new Set();
                ['Gender', 'StatusAbsensi', 'StatusVerifikasi', 'StatusKegiatan', 'StatusPeriode'].forEach(e => {
                    if (new RegExp(`\\b${e}\\b`).test(content)) {
                        enumsUsed.add(e);
                    }
                });
                
                if (enumsUsed.size > 0) {
                    const imports = Array.from(enumsUsed).map(e => `import com.bsi.manajement_magang.enums.${e};`).join('\n');
                    if (!content.includes(imports)) {
                       content = content.replace(/(package\s+[^;]+;)/, `$1\n\n${imports}`);
                    }
                }
                fs.writeFileSync(filePath, content, 'utf8');
                console.log(`Updated ${filePath}`);
            }
        }
    }
}

processDirectory(basePath);
console.log('Refactoring complete for Request/Response.');
