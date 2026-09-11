import { mkdirSync, writeFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { Resvg } from '@resvg/resvg-js'
import { scaleLinear } from 'd3-scale'
import { select } from 'd3-selection'
import { parseHTML } from 'linkedom'

const WIDTH = 800
const HEIGHT = 600
const MARGIN_X = 150
const MARGIN_Y = 175

export const MAX_LATITUDE = 90
export const MAX_LONGITUDE = 180

const HALF_SIZE = 25
const ARROW_GAP = 8

export type Coordinate = { latitude: number; longitude: number }

function createTemplate() {
    const { document } = parseHTML('<html><body></body></html>')

    const svg = select(document.body)
        .append('svg')
        .attr('xmlns', 'http://www.w3.org/2000/svg')
        .attr('width', WIDTH)
        .attr('height', HEIGHT)

    svg.append('rect')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('fill', 'white')

    const x = scaleLinear()
        .domain([-MAX_LONGITUDE, MAX_LONGITUDE])
        .range([MARGIN_X, WIDTH - MARGIN_X])
    const y = scaleLinear()
        .domain([-MAX_LATITUDE, MAX_LATITUDE])
        .range([HEIGHT - MARGIN_Y, MARGIN_Y])

    svg.append('rect')
        .attr('x', x(-MAX_LONGITUDE))
        .attr('y', y(MAX_LATITUDE))
        .attr('width', x(MAX_LONGITUDE) - x(-MAX_LONGITUDE))
        .attr('height', y(-MAX_LATITUDE) - y(MAX_LATITUDE))
        .attr('fill', 'none')
        .attr('stroke', 'black')
        .attr('stroke-width', 2)

    const labels = svg
        .append('g')
        .attr('font-family', 'sans-serif')
        .attr('font-size', 16)
        .attr('fill', 'black')

    labels
        .append('text')
        .attr('x', x(0))
        .attr('y', y(MAX_LATITUDE) - 10)
        .attr('text-anchor', 'middle')
        .text(`max. lat = ${MAX_LATITUDE}°`)
    labels
        .append('text')
        .attr('x', x(0))
        .attr('y', y(-MAX_LATITUDE) + 24)
        .attr('text-anchor', 'middle')
        .text(`min. lat = ${-MAX_LATITUDE}°`)
    labels
        .append('text')
        .attr('x', x(-MAX_LONGITUDE) - 10)
        .attr('y', y(0))
        .attr('text-anchor', 'end')
        .attr('dominant-baseline', 'middle')
        .text(`min. lon = ${-MAX_LONGITUDE}°`)
    labels
        .append('text')
        .attr('x', x(MAX_LONGITUDE) + 10)
        .attr('y', y(0))
        .attr('text-anchor', 'start')
        .attr('dominant-baseline', 'middle')
        .text(`max. lon = ${MAX_LONGITUDE}°`)

    return { svg, x, y }
}

export type Figure = ReturnType<typeof createTemplate>

export function renderVariant(name: string, draw: (figure: Figure) => void) {
    const [outputArg] = process.argv.slice(2)
    if (!outputArg) {
        console.error(`Usage: ${name} <output.png>`)
        process.exit(1)
    }
    const output = resolve(outputArg)

    const figure = createTemplate()
    draw(figure)

    const png = new Resvg(figure.svg.node()!.outerHTML).render().asPng()

    mkdirSync(dirname(output), { recursive: true })
    writeFileSync(output, png)
    console.log(`Wrote ${output}`)
}

export type Box = { west: number; east: number; south: number; north: number }

type PointStyle = {
    color?: string
    boxes?: Box[]
    shifted?: boolean
    labelSide?: 'left' | 'right'
    labelOffset?: number
}

export function squareAround({ latitude, longitude }: Coordinate): Box {
    return {
        west: longitude - HALF_SIZE,
        east: longitude + HALF_SIZE,
        south: latitude - HALF_SIZE,
        north: latitude + HALF_SIZE,
    }
}

function clamp(value: number, limit: number) {
    return Math.min(limit, Math.max(-limit, value))
}

// Mirrors AnywhereTaskUtils.clampCoordinate, which wraps rather than clamps.
function wrap(value: number, limit: number) {
    if (value >= -limit && value <= limit) {
        return value
    }

    const period = 2 * limit
    const wrapped = ((((value + limit) % period) + period) % period) - limit
    return wrapped === -limit && value > 0 ? limit : wrapped
}

export function clampLatitude({ latitude, longitude }: Coordinate): Coordinate {
    return { latitude: clamp(latitude, MAX_LATITUDE), longitude }
}

export function wrapCoordinate({
    latitude,
    longitude,
}: Coordinate): Coordinate {
    return { latitude, longitude: wrap(longitude, MAX_LONGITUDE) }
}

export function offsetBetween(from: Coordinate, to: Coordinate): Coordinate {
    return {
        latitude: to.latitude - from.latitude,
        longitude: to.longitude - from.longitude,
    }
}

// Mirrors AnywhereTask.shiftBound: latitude is clamped and each longitude edge
// is wrapped on its own, so a box can come out crossing the antimeridian. That
// box is returned as two pieces, one either side of the edge. A box spanning
// every longitude keeps its original edges.
export function shiftBox(
    { west, east, south, north }: Box,
    offset: Coordinate
): Box[] {
    const latitudes = {
        south: clamp(south + offset.latitude, MAX_LATITUDE),
        north: clamp(north + offset.latitude, MAX_LATITUDE),
    }
    if (east - west >= 2 * MAX_LONGITUDE) {
        return [{ west, east, ...latitudes }]
    }

    const left = wrap(west + offset.longitude, MAX_LONGITUDE)
    const right = wrap(east + offset.longitude, MAX_LONGITUDE)
    if (left <= right) {
        return [{ west: left, east: right, ...latitudes }]
    }
    return [
        { west: left, east: MAX_LONGITUDE, ...latitudes },
        { west: -MAX_LONGITUDE, east: right, ...latitudes },
    ]
}

export function drawBoxes(
    { svg, x, y }: Figure,
    boxes: Box[],
    {
        color = 'red',
        shifted = false,
    }: { color?: string; shifted?: boolean } = {}
) {
    for (const box of boxes) {
        const rect = svg
            .append('rect')
            .attr('x', x(box.west))
            .attr('y', y(box.north))
            .attr('width', x(box.east) - x(box.west))
            .attr('height', y(box.south) - y(box.north))
            .attr('stroke', color)
            .attr('stroke-width', 1.5)
        if (shifted) {
            rect.attr('fill', color).attr('fill-opacity', 0.15)
        } else {
            rect.attr('fill', 'none').attr('stroke-dasharray', '4 3')
        }
    }
}

export function drawPoint(
    { svg, x, y }: Figure,
    coordinate: Coordinate,
    {
        color = 'red',
        boxes = [squareAround(coordinate)],
        shifted = false,
        labelSide = 'left',
        labelOffset = 0,
    }: PointStyle = {}
) {
    const { latitude, longitude } = coordinate

    drawBoxes({ svg, x, y }, boxes, { color, shifted })

    svg.append('circle')
        .attr('cx', x(longitude))
        .attr('cy', y(latitude))
        .attr('r', 3)
        .attr('fill', color)

    const labelBox = boxes.find(
        (box) => box.west <= longitude && longitude <= box.east
    ) ??
        boxes[0] ?? { west: longitude, east: longitude }
    svg.append('text')
        .attr(
            'x',
            labelSide === 'left' ? x(labelBox.west) - 8 : x(labelBox.east) + 8
        )
        .attr('y', y(latitude) + labelOffset)
        .attr('text-anchor', labelSide === 'left' ? 'end' : 'start')
        .attr('dominant-baseline', 'middle')
        .attr('font-family', 'sans-serif')
        .attr('font-size', 14)
        .attr('fill', color)
        .text(`(${latitude}°, ${longitude}°)`)
}

export function drawArrow(
    { svg, x, y }: Figure,
    from: Coordinate,
    to: Coordinate
) {
    svg.append('defs')
        .append('marker')
        .attr('id', 'arrowhead')
        .attr('viewBox', '0 0 10 10')
        .attr('refX', 10)
        .attr('refY', 5)
        .attr('markerWidth', 5)
        .attr('markerHeight', 5)
        .attr('orient', 'auto')
        .append('path')
        .attr('d', 'M 0 0 L 10 5 L 0 10 z')
        .attr('fill', 'red')

    const x1 = x(from.longitude)
    const y1 = y(from.latitude)
    const x2 = x(to.longitude)
    const y2 = y(to.latitude)
    const length = Math.hypot(x2 - x1, y2 - y1)
    const ux = (x2 - x1) / length
    const uy = (y2 - y1) / length

    svg.append('line')
        .attr('x1', x1 + ux * ARROW_GAP)
        .attr('y1', y1 + uy * ARROW_GAP)
        .attr('x2', x2 - ux * ARROW_GAP)
        .attr('y2', y2 - uy * ARROW_GAP)
        .attr('stroke', 'red')
        .attr('stroke-width', 1.5)
        .attr('marker-end', 'url(#arrowhead)')
}

export function drawTransform(
    figure: Figure,
    from: Coordinate,
    to: Coordinate
) {
    drawPoint(figure, from)
    drawPoint(figure, to)
    drawArrow(figure, from, to)
}
