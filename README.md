# JetBrains Quick Select

[![Version](https://img.shields.io/badge/version-0.0.1-blue.svg)](https://github.com/kj455/jetbrains-quick-select)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

A JetBrains IDE plugin for quick and simple text selection. This is a port of [vscode-quick-select](https://github.com/dbankier/vscode-quick-select) by David Bankier, bringing VIM-inspired text selection capabilities to JetBrains IDEs.

## Features

Jump to select between quotes, brackets, and more with simple keyboard shortcuts. The plugin automatically detects and selects text within matching pairs, with support for:

- **Nested brackets** - Correctly handles deeply nested structures
- **Multiline selection** - Especially for backticks
- **Auto-expand** - Press the same shortcut twice to expand selection to include the surrounding brackets/quotes

## Keyboard Shortcuts

All shortcuts follow the pattern `Cmd+K` (or `Ctrl+K` on Windows/Linux) followed by a character:

### Quotes
- `Cmd+K, '` - Select inside single quotes `'text'`
- `Cmd+K, "` - Select inside double quotes `"text"` (Cmd+K, Shift+')
- ``Cmd+K, ` `` - Select inside backticks `` `text` ``

### Brackets
- `Cmd+K, (` - Select inside parentheses `(text)` (Cmd+K, Shift+9)
- `Cmd+K, [` - Select inside square brackets `[text]`
- `Cmd+K, {` - Select inside curly brackets `{text}` (Cmd+K, Shift+[)

### Auto-expand Selection
When you already have a selection inside quotes/brackets, pressing the same shortcut again will expand the selection to include the surrounding characters:

```
First press:  "hello|"  →  "|hello|"
Second press: "|hello|"  →  |"hello"|
```

## Credits

This plugin is a port of the excellent [vscode-quick-select](https://github.com/dbankier/vscode-quick-select) extension by [David Bankier](https://github.com/dbankier). The original VS Code extension is licensed under the MIT License.

## License

MIT License

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Issues

Found a bug or have a feature request? Please open an issue on [GitHub Issues](https://github.com/kj455/jetbrains-quick-select/issues).
