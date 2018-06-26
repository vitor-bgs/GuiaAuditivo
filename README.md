# Testes Guia Auditivo

Testes para o projeto Guia Auditivo

O propósito deste projeto é desenvolver um App que auxilie deficientes visuais a se locomoverem em ambientes internos.
O App Guia Auditivo utilizará as intensidades do sinal de Wi-Fi para determinar a posição do usuário e guiá-lo.

Partes do App:

Cadastro/Treinamento - O mapa do local é exibido na tela para iniciar o cadastramento/mapeamento do ambiente. Ao clicar em uma parte da tela as intensidades do Wi-Fi são medidas e cadastradas em um banco de dados, relacionando as intensidades às coordenadas.
Uma coordenada poderá ter um nome. Este nome será informado ao usuário toda vez que ele passar por essa coordenada. O nome servirá também para pesquisar as coordenadas do local e traçar uma rota até ele.

Posicionamento - O usuário caminha pelo local onde as coordenadas já foram cadastradas. O App mede as intensidades dos pontos de acesso Wi-Fi e busca no banco de dados as coordenadas correspondentes. Caso a coordenada possua um nome, será informado ao usuário o local ao qual ele está e os locais mais próximos.

Guia - O usuário diz ao App o local ao qual ele deseja ir, o app calcula a rota e guia o usuário até o local desejado.

Identificação de Objetos - Para dispositivos que possuam leitor NFC, o app irá ler a etiqueta NFC e buscar em um banco de dados o objeto.
