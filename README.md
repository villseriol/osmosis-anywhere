# Osmosis Anywhere

This plugin transforms coordinates in OpenStreetMap data by any user-designated amount.

## Examples

The following are illustrations of how the plugin handles coordinate transforms. For all
coordinates:

- The latitude value is between +-90 degrees
- The longitude value is between +-180 degrees

### Transformed Coordinate & Bounds Inside Limits

This is a golden-path example where a node (an its bounds) latitude and longitude values were
transformed to an area inside the limits.

![Coordinate (34°, 135°) and its bounds shifted to (-32°, 116°), both still inside the limits](images/bounds-within.png)

## Disclaimer

## License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not,
see <https://www.gnu.org/licenses/>.
