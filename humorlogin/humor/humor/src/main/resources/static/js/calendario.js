document.addEventListener("DOMContentLoaded", () => {
    const calendar = document.getElementById("calendar");
    const modal = document.getElementById("modal");
    const registrosLista = document.getElementById("registros-lista");
    const modalData = document.getElementById("modal-data");
    const closeBtn = document.getElementById("close-btn");

    const title = document.getElementById("calendar-title");
    const btnPrev = document.getElementById("prev-month");
    const btnNext = document.getElementById("next-month");

    let anoAtual = new Date().getFullYear();
    let mesAtual = new Date().getMonth(); // 0 = Janeiro

    // Carrega o resumo (todos os registros)
    let resumoCache = null;

    fetch("/dashboard/registros/resumo")
        .then(res => res.json())
        .then(data => {
            resumoCache = data;
            renderCalendar();
        })
        .catch(err => console.error("Erro ao carregar resumo:", err));

    // Renderiza o calendário do mês atual
    function renderCalendar() {
        calendar.innerHTML = "";

        const primeiroDia = new Date(anoAtual, mesAtual, 1);
        const ultimoDia = new Date(anoAtual, mesAtual + 1, 0);

        const nomesMeses = [
            "Janeiro","Fevereiro","Março","Abril","Maio","Junho",
            "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"
        ];

        title.textContent = `${nomesMeses[mesAtual]} de ${anoAtual}`;

        for (let dia = 1; dia <= ultimoDia.getDate(); dia++) {
            const dataStr = `${anoAtual}-${String(mesAtual + 1).padStart(2, "0")}-${String(dia).padStart(2, "0")}`;

            const humor = resumoCache ? resumoCache[dataStr] : null;

            const div = document.createElement("div");
            div.classList.add("day");
            if (humor) div.classList.add(humor);
            div.textContent = dia;

            div.onclick = () => abrirModal(dataStr);
            calendar.appendChild(div);
        }
    }

    // Abrir modal
    function abrirModal(dataStr) {
        modal.style.display = "flex";
        modalData.textContent = `Registros de ${dataStr}`;
        registrosLista.innerHTML = "<p>Carregando...</p>";

        fetch(`/dashboard/registros/${dataStr}`)
            .then(res => res.json())
            .then(registros => {
                if (registros.length === 0) {
                    registrosLista.innerHTML = "<p>Nenhum registro neste dia.</p>";
                    return;
                }

                registrosLista.innerHTML = registros.map(r => `
                    <div class="registro-card">
                        <strong>${r.status}</strong><br>
                        <p>${r.texto}</p>
                        ${r.tags && r.tags.length > 0 ? `<small><em>Tags: ${r.tags.join(", ")}</em></small><br>` : ""}
                        <small>${new Date(r.dataHora).toLocaleString()}</small>
                    </div>
                `).join("");
            })
            .catch(err => {
                registrosLista.innerHTML = "<p>Erro ao carregar registros.</p>";
                console.error(err);
            });
    }

    // Botão anterior
    btnPrev.onclick = () => {
        mesAtual--;
        if (mesAtual < 0) {
            mesAtual = 11;
            anoAtual--;
        }
        renderCalendar();
    };

    // Botão próximo
    btnNext.onclick = () => {
        mesAtual++;
        if (mesAtual > 11) {
            mesAtual = 0;
            anoAtual++;
        }
        renderCalendar();
    };

    // Fechar modal
    closeBtn.onclick = () => modal.style.display = "none";
    modal.onclick = e => { if (e.target === modal) modal.style.display = "none"; };
});
