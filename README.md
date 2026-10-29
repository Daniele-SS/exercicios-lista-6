# Pokédex — Lista 6 (Programação para Dispositivos Mobile)

App Android nativo (Kotlin + Jetpack Compose) que consome a [PokéAPI](https://pokeapi.co/docs/v2), seguindo a arquitetura **MVVM**.

## Requisitos atendidos

| Requisito | Implementação |
|---|---|
| Tela inicial lista pokémons | `ui/list/PokemonListScreen.kt` — grade (padrão, igual ao protótipo) **e** lista, alternáveis pelo ícone da barra superior |
| Pesquisa por nome ou ID | `model/PokemonSearch.kt` — nome parcial (`pika`), ID (`25`, `#025`), espaço ↔ hífen (`mr mime`) |
| Detalhes do pokémon | `ui/detail/PokemonDetailScreen.kt` — tipos, peso, altura, habilidades, descrição e base stats |
| Cores pelo tipo principal | Cards (borda, ID e faixa do nome) e fundo dos detalhes usam a cor do 1º tipo |
| **Desafio:** cores com enum com construtor | `model/PokemonType.kt` — `enum class PokemonType(val color: Color)` |
| **Desafio:** evoluções e involuções | `ui/detail/DetailSections.kt` (`EvolutionSection`) — cadeia completa por estágio, pokémon atual destacado, toque navega para o outro pokémon |

## Arquitetura (MVVM)

```
View (Compose)            ui/list, ui/detail, ui/components
   │  observa StateFlow / eventos de UI
ViewModel                 PokemonListViewModel, PokemonDetailViewModel
   │  chama
Repository                data/repository/PokemonRepository (cache em memória)
   │  Retrofit + Gson
PokéAPI                   data/remote/PokeApiService + dto/
                          data/mapper converte DTO -> model (domínio)
```

- Injeção de dependência manual (`di/AppContainer`, criado em `PokedexApplication`), ViewModels via `viewModelFactory`.
- O ID do pokémon chega ao `PokemonDetailViewModel` pelo `SavedStateHandle` (argumento da rota do Navigation Compose).

## Decisões técnicas

- **Busca local:** a PokéAPI não tem busca parcial por nome. A lista completa (`/pokemon?limit=1025`) é baixada uma vez e filtrada em memória — 1025 itens filtram em microssegundos.
- **Cor dos cards sem N requisições:** `/pokemon?limit=…` não informa tipos. Em vez de 1025 chamadas a `/pokemon/{id}`, o app faz **18 chamadas** a `/type/{nome}` em paralelo; o campo `slot` de cada entrada indica se o tipo é primário ou secundário.
- **Cadeia evolutiva:** `/pokemon-species/{id}` → `evolution_chain.url` → árvore achatada por estágio. Em cadeias ramificadas (ex.: Eevee, Wurmple) os ramos aparecem lado a lado no mesmo estágio.
- **Imagens:** official artwork do repositório `PokeAPI/sprites`, carregadas com Coil.
- Textos do protótipo foram mantidos (About, Base Stats…); a descrição vem em inglês porque a PokéAPI quase não tem `flavor_text` em pt-BR.

## Como executar

1. Abrir a pasta no Android Studio (Ladybug ou mais recente) e aguardar o Gradle sync.
2. Rodar o módulo `app` em um emulador/dispositivo com Android 7.0+ (API 24) e acesso à internet.

Testes unitários (busca, mapeamento de tipos, cadeia evolutiva, formatação):

```bash
./gradlew testDebugUnitTest
```

## Stack

Kotlin 2.0.21 · AGP 8.7.3 · Compose BOM 2024.12.01 (Material 3) · Navigation Compose · Lifecycle ViewModel/StateFlow · Retrofit 2.11 + Gson · Coil 2.7
