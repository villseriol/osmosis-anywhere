import {
    clampLatitude,
    drawArrow,
    drawPoint,
    offsetBetween,
    renderVariant,
    shiftBox,
    squareAround,
} from '@/coordinate-bounds-template'

const FROM = { latitude: 34, longitude: 135 }
const TO = { latitude: 100, longitude: 70 }

renderVariant('generate-bounds-latitude-invalid', (figure) => {
    drawPoint(figure, FROM)
    drawPoint(figure, TO, { labelSide: 'right' })
    drawArrow(figure, FROM, TO)
    drawPoint(figure, clampLatitude(TO), {
        color: 'blue',
        boxes: shiftBox(squareAround(FROM), offsetBetween(FROM, TO)),
        shifted: true,
        labelOffset: 14,
    })
})
